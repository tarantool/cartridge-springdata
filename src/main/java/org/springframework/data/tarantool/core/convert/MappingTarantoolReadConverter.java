package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.EntityReader;
import org.springframework.data.convert.TypeAliasAccessor;
import org.springframework.data.convert.TypeMapper;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.mapping.model.PersistentEntityParameterValueProvider;
import org.springframework.data.mapping.model.PropertyValueProvider;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentProperty;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mapping converter for Tarantool for reading objects from tuples
 *
 * @author Alexey Kuzin
 * @author Artyom Dubinin
 */
public class MappingTarantoolReadConverter implements EntityReader<Object, Object> {

    private final EntityInstantiators instantiators;
    private final TarantoolMappingContext mappingContext;
    private final TypeMapper<TarantoolTuple> typeMapper;
    private final TypeMapper<Map<String, Object>> mapTypeMapper;
    private final TypeAliasAccessor<Map<String, Object>> mapTypeAliasAccessor;
    private final CustomConversions conversions;
    private final GenericConversionService conversionService;

    public MappingTarantoolReadConverter(EntityInstantiators instantiators,
                                         TarantoolMappingContext mappingContext,
                                         TypeMapper<TarantoolTuple> typeMapper,
                                         TypeMapper<Map<String, Object>> mapTypeMapper,
                                         TypeAliasAccessor<Map<String, Object>> mapTypeAliasAccessor,
                                         CustomConversions conversions,
                                         GenericConversionService conversionService) {
        this.instantiators = instantiators;
        this.mappingContext = mappingContext;
        this.typeMapper = typeMapper;
        this.mapTypeMapper = mapTypeMapper;
        this.mapTypeAliasAccessor = mapTypeAliasAccessor;
        this.conversions = conversions;
        this.conversionService = conversionService;
    }

    @Nullable
    private <R> R read(Class<R> targetClass, final @Nullable Map<String, Object> source) {
        if (source == null) {
            return null;
        }

        TypeInformation<? extends R> typeToUse = mapTypeMapper.readType(source, ClassTypeInformation.from(targetClass));
        Class<? extends R> rawType = typeToUse.getType();

        if (conversions.hasCustomReadTarget(Map.class, rawType)) {
            return conversionService.convert(source, rawType);
        }

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(typeToUse);
        TarantoolPropertyValueProvider propertyValueProvider = getPropertyValueProvider(source);
        ConvertingPropertyAccessor<?> accessor = getConvertingPropertyAccessor(entity, propertyValueProvider);

        return convertProperties(entity, propertyValueProvider, accessor);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <R> R read(Class<R> targetClass, final @Nullable Object source) {
        if (source == null) {
            return null;
        }
        if (targetClass.equals(void.class)) {
            throw new MappingException(
                    String.format("Cannot map object of type %s to object of type %s", source.getClass(), targetClass)
            );
        }

        TypeInformation<? extends R> typeToUse;
        if (source instanceof TarantoolTuple) {
            TarantoolTuple tuple = (TarantoolTuple) source;
            if (tuple.hasMetadata()) {
                throw new MappingException(
                        String.format("Cannot map object to entity without metadata")
                );
            }
            typeToUse = typeMapper.readType(tuple, ClassTypeInformation.from(targetClass));

            Class<? extends R> rawType = typeToUse.getType();
            if (conversions.hasCustomReadTarget(TarantoolTuple.class, rawType)) {
                return conversionService.convert(source, rawType);
            }
        } else if (source instanceof Map) {
            typeToUse = mapTypeMapper.readType((Map<String, Object>) source, ClassTypeInformation.from(targetClass));
        } else if (source.getClass().equals(targetClass)) {
            return (R) source;
        } else {
            throw new MappingException(
                    String.format("Cannot map object of type %s to object of type %s", source.getClass(), targetClass)
            );
        }

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(typeToUse);
        TarantoolPropertyValueProvider propertyValueProvider = getPropertyValueProvider(source);
        ConvertingPropertyAccessor<?> accessor = getConvertingPropertyAccessor(entity, propertyValueProvider);

        return convertProperties(entity, propertyValueProvider, accessor);
    }

    private TarantoolPropertyValueProvider getPropertyValueProvider(Object source) {
        return new TarantoolPropertyValueProvider(source, conversions, conversionService);
    }

    private ParameterValueProvider<TarantoolPersistentProperty> getParameterProvider(
            TarantoolPersistentEntity<?> entity, TarantoolPropertyValueProvider propertyValueProvider) {
        return new PersistentEntityParameterValueProvider<>(entity, propertyValueProvider, null);
    }

    private ConvertingPropertyAccessor<?> getConvertingPropertyAccessor(
            TarantoolPersistentEntity<?> entity,
            TarantoolPropertyValueProvider propertyValueProvider
    ) {
        EntityInstantiator instantiator = instantiators.getInstantiatorFor(entity);
        ParameterValueProvider<TarantoolPersistentProperty> provider = getParameterProvider(
                entity, propertyValueProvider
        );
        Object instance = instantiator.createInstance(entity, provider);
        PersistentPropertyAccessor<?> accessor = entity.getPropertyAccessor(instance);

        return new ConvertingPropertyAccessor<>(accessor, conversionService);
    }

    private <R> R convertProperties(TarantoolPersistentEntity<?> entity,
                                    TarantoolPropertyValueProvider propertyValueProvider,
                                    PersistentPropertyAccessor<?> propertyAccessor) {
        entity.doWithProperties((PropertyHandler<TarantoolPersistentProperty>) property -> {
            if (entity.isCreatorArgument(property)) {
                return;
            }
            setProperty(property, propertyValueProvider, propertyAccessor);
        });

        entity.doWithAssociations((AssociationHandler<TarantoolPersistentProperty>) association -> {
            TarantoolPersistentProperty inverseProperty = association.getInverse();
            setProperty(inverseProperty, propertyValueProvider, propertyAccessor);
        });

        return (R) propertyAccessor.getBean();
    }

    private void setProperty(TarantoolPersistentProperty property,
                             TarantoolPropertyValueProvider propertyValueProvider,
                             PersistentPropertyAccessor<?> propertyAccessor) {
        Object propValue = propertyValueProvider.getPropertyValue(property);
        if (property.getType().isPrimitive() && propValue == null) {
            return;
        }
        propertyAccessor.setProperty(property, propValue);
    }

    /**
     * A {@link PropertyValueProvider} to get field values from a tuple
     *
     * @author Alexey Kuzin
     */
    private class TarantoolPropertyValueProvider implements PropertyValueProvider<TarantoolPersistentProperty> {

        private Object source;
        private CustomConversions conversions;
        private ConversionService conversionService;
        private final TypeMapper<Map<String, Object>> mapTypeMapper;

        TarantoolPropertyValueProvider(Object source,
                                              CustomConversions conversions,
                                              ConversionService conversionService) {
            this.source = source;
            this.conversions = conversions;
            this.conversionService = conversionService;
            this.mapTypeMapper = new TarantoolMapTypeMapper();
        }

        @Override
        @Nullable
        @SuppressWarnings("unchecked")
        public <R> R getPropertyValue(TarantoolPersistentProperty property) {

            if (source == null) {
                return null;
            }

            TypeInformation<?> propType = property.getTypeInformation();
            Class<?> propClass = propType.getType();
            Optional<Class<?>> customTargetClass = conversions.getCustomWriteTarget(propClass);

            String fieldName = property.getFieldName();
            if (source instanceof TarantoolTuple) {
                Object value;
                if (propType.isCollectionLike()) {
                    value = ((TarantoolTuple) source).getList(fieldName);
                } else if (propType.isMap()) {
                    value = ((TarantoolTuple) source).getMap(fieldName);
                } else if (customTargetClass.isPresent() &&
                        conversions.hasCustomReadTarget(customTargetClass.get(), propClass) &&
                        ((TarantoolTuple) source).canGetObject(fieldName, customTargetClass.get())) {
                    value = ((TarantoolTuple) source).getObject(fieldName, customTargetClass.get()).orElse(null);
                } else if (((TarantoolTuple) source).canGetObject(fieldName, propClass)) {
                    value = ((TarantoolTuple) source).getObject(fieldName, propClass).orElse(null);
                } else {
                    value = ((TarantoolTuple) source).getObject(fieldName).orElse(null);
                }
                return readValue(value, propType);
            } else if (source instanceof Map) {
                return readValue(((Map<String, Object>) source).get(fieldName), propType);
            } else {
                throw new MappingException("Cannot read properties from a source of type " + source.getClass());
            }
        }

        private <R> R readValue(@Nullable Object source, TypeInformation<?> propertyType) {
            Assert.notNull(propertyType, "Target type must not be null!");

            if (source == null) {
                return null;
            }

            Class<?> targetClass = propertyType.getType();
            if (conversions.hasCustomReadTarget(source.getClass(), targetClass)
                    || conversions.isSimpleType(targetClass)
                    && conversionService.canConvert(source.getClass(), targetClass)) {
                return (R) conversionService.convert(source, targetClass);
            } else if (propertyType.isCollectionLike()) {
                return convertCollection(asCollection(source), propertyType);
            } else if (propertyType.isMap()) {
                return (R) convertMap((Map<String, Object>) source, propertyType);
            }
            return (R) convertIfNeeded(source, propertyType);
        }

        private Collection<?> asCollection(Object source) {
            if (source instanceof Collection) {
                return (Collection<?>) source;
            }
            return source.getClass().isArray() ? CollectionUtils.arrayToList(source) : Collections.singleton(source);
        }

        private <R> R convertCustomType(Map<String, Object> source, TypeInformation<?> propertyType) {
            if (source == null) {
                return null;
            }

            TypeInformation<?> typeToUse = mapTypeMapper.readType(source, propertyType);
            TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(typeToUse);
            if (shouldDefaultToMap(source, entity)) {
                return (R) source;
            }
            PropertyValueProvider<TarantoolPersistentProperty> propertyValueProvider =
                    new PropertyValueProvider<TarantoolPersistentProperty>() {
                        @Override
                        public <T> T getPropertyValue(TarantoolPersistentProperty property) {
                            TypeInformation<?> propType = property.getTypeInformation();
                            return readValue(source.get(property.getFieldName()), propType);
                        }
                    };
            EntityInstantiator instantiator = instantiators.getInstantiatorFor(entity);
            ParameterValueProvider<TarantoolPersistentProperty> provider =
                    new PersistentEntityParameterValueProvider<>(entity, propertyValueProvider, null);
            Object instance = instantiator.createInstance(entity, provider);
            PersistentPropertyAccessor<?> propertyAccessor =
                    new ConvertingPropertyAccessor<>(entity.getPropertyAccessor(instance), conversionService);
            entity.doWithProperties((PropertyHandler<TarantoolPersistentProperty>) property -> {
                if (entity.isCreatorArgument(property)) {
                    return;
                }
                Object propValue = propertyValueProvider.getPropertyValue(property);
                if (property.getType().isPrimitive() && propValue == null) {
                    return;
                }
                propertyAccessor.setProperty(property, propValue);
            });
            return (R) propertyAccessor.getBean();
        }

        private boolean shouldDefaultToMap(Map<String, Object> source, TarantoolPersistentEntity<?> entity) {
            return entity == null && !mapTypeAliasAccessor.readAliasFrom(source).isPresent();
        }

        private <R> R convertCollection(final Collection<?> source, final TypeInformation<?> propertyType) {
            Class<?> collectionClass = propertyType.getType();
            TypeInformation<?> elementType = propertyType.getComponentType();
            Class<?> elementClass = elementType == null ? null : elementType.getType();

            Collection<Object> items = collectionClass.isArray() ? new ArrayList<>() :
                    CollectionFactory.createCollection(collectionClass, elementClass, source.size());

            source.forEach(item -> items.add(readValue(item, elementType)));

            return (R) convertIfNeeded(items, propertyType);
        }

        private <R> R convertMap(Map<String, Object> source, TypeInformation<?> propertyType) {
            Class<?> mapClass = propertyType.getType();
            TypeInformation<?> keyType = propertyType.getComponentType();
            Class<?> keyClass = keyType == null ? null : keyType.getType();
            TypeInformation<?> mapValueType = propertyType.getMapValueType();

            Class<?> mapType = mapTypeMapper.readType(source, propertyType).getType();

            Map<Object, Object> converted = mapType != null ?
                    CollectionFactory.createMap(mapClass, keyClass, source.keySet().size()) :
                    new HashMap<>();

            source.forEach((key, value) -> converted.put(key, readValue(value, mapValueType)));

            return (R) convertIfNeeded(converted, propertyType);
        }

        private Object convertIfNeeded(Object value, TypeInformation<?> propertyType) {
            Class<?> targetClass = propertyType.getType();
            if (Enum.class.isAssignableFrom(targetClass)) {
                return Enum.valueOf((Class<Enum>) targetClass, value.toString());
            } else if (value instanceof Map && !propertyType.isMap()) {
                return convertCustomType((Map<String, Object>) value, propertyType);
            } else {
                return value;
            }
        }
    }
}
