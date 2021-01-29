package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.EntityInstantiator;
import org.springframework.data.convert.EntityInstantiators;
import org.springframework.data.convert.EntityReader;
import org.springframework.data.convert.TypeAliasAccessor;
import org.springframework.data.convert.TypeMapper;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
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
import java.util.Map;
import java.util.Optional;

/**
 * Mapping converter for Tarantool for reading objects from tuples
 *
 * @author Alexey Kuzin
 */
public class MappingTarantoolReadConverter implements EntityReader<Object, TarantoolTuple> {

    private final EntityInstantiators instantiators;
    private final TarantoolMappingContext mappingContext;
    private final TypeMapper<TarantoolTuple> typeMapper;
    private final TypeAliasAccessor<Map<String, Object>> mapTypeAliasAccessor;
    private final CustomConversions conversions;
    private final GenericConversionService conversionService;

    public MappingTarantoolReadConverter(EntityInstantiators instantiators,
                                         TarantoolMappingContext mappingContext,
                                         TypeMapper<TarantoolTuple> typeMapper,
                                         TypeAliasAccessor<Map<String, Object>> mapTypeAliasAccessor,
                                         CustomConversions conversions,
                                         GenericConversionService conversionService) {
        this.instantiators = instantiators;
        this.mappingContext = mappingContext;
        this.typeMapper = typeMapper;
        this.mapTypeAliasAccessor = mapTypeAliasAccessor;
        this.conversions = conversions;
        this.conversionService = conversionService;
    }

    @Override
    @Nullable
    public <R> R read(Class<R> targetClass, final @Nullable TarantoolTuple source) {
        if (source == null) {
            return null;
        }

        TypeInformation<? extends R> typeToUse = typeMapper.readType(source, ClassTypeInformation.from(targetClass));
        Class<? extends R> rawType = typeToUse.getType();

        if (conversions.hasCustomReadTarget(TarantoolTuple.class, rawType)) {
            return conversionService.convert(source, rawType);
        }

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(typeToUse);
        TarantoolPropertyValueProvider propertyValueProvider = getPropertyValueProvider(source);
        ConvertingPropertyAccessor<?> accessor = getConvertingPropertyAccessor(entity, propertyValueProvider);

        return convertProperties(entity, propertyValueProvider, accessor);
    }

    private TarantoolPropertyValueProvider getPropertyValueProvider(TarantoolTuple source) {
        return new TarantoolPropertyValueProvider(source, conversions, conversionService);
    }

    private ParameterValueProvider<TarantoolPersistentProperty> getParameterProvider(
            TarantoolPersistentEntity<?> entity, TarantoolPropertyValueProvider propertyValueProvider) {
        return new PersistentEntityParameterValueProvider<>(entity, propertyValueProvider, null);
    }

    private ConvertingPropertyAccessor<?> getConvertingPropertyAccessor(TarantoolPersistentEntity<?> entity,
                                                                        TarantoolPropertyValueProvider propertyValueProvider) {
        EntityInstantiator instantiator = instantiators.getInstantiatorFor(entity);
        ParameterValueProvider<TarantoolPersistentProperty> provider = getParameterProvider(entity, propertyValueProvider);
        Object instance = instantiator.createInstance(entity, provider);
        PersistentPropertyAccessor<?> accessor = entity.getPropertyAccessor(instance);

        return new ConvertingPropertyAccessor<>(accessor, conversionService);
    }

    private <R> R convertProperties(TarantoolPersistentEntity<?> entity,
                                    TarantoolPropertyValueProvider propertyValueProvider,
                                    PersistentPropertyAccessor<?> propertyAccessor) {
        entity.doWithProperties((PropertyHandler<TarantoolPersistentProperty>) property -> {
            if (entity.isConstructorArgument(property)) {
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

        private TarantoolTuple source;
        private CustomConversions conversions;
        private ConversionService conversionService;
        private final TypeMapper<Map<String, Object>> mapTypeMapper;

        public TarantoolPropertyValueProvider(TarantoolTuple source,
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
            Object value;
            if (propType.isCollectionLike()) {
                value = source.getList(property.getFieldName());
            } else if (propType.isMap()) {
                value = source.getMap(property.getFieldName());
            } else if (customTargetClass.isPresent() &&
                    conversions.hasCustomReadTarget(customTargetClass.get(), propClass)) {
                value = source.getObject(property.getFieldName(), customTargetClass.get()).orElse(null);
            } else if (conversions.isSimpleType(propClass)) {
                value = source.getObject(property.getFieldName(), propClass).orElse(null);
            } else {
                value = convertCustomType(source.getMap(property.getFieldName()), propType);
            }
            return readValue(value, propType);
        }

        private <R> R readValue(@Nullable Object source, TypeInformation<?> propertyType) {
            Assert.notNull(propertyType, "Target type must not be null!");

            if (source == null) {
                return null;
            }

            Class<?> targetClass = propertyType.getType();
            if (conversions.hasCustomReadTarget(source.getClass(), targetClass)) {
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
            TypeInformation<?> typeToUse = mapTypeMapper.readType(source, propertyType);
            TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(typeToUse);
            if (shouldDefaultToMap(source, entity)) {
                return (R) source;
            }
            PropertyValueProvider<TarantoolPersistentProperty> propertyValueProvider = new PropertyValueProvider<TarantoolPersistentProperty>() {
                @Override
                public <T> T getPropertyValue(TarantoolPersistentProperty property) {
                    TypeInformation<?> propType = property.getTypeInformation();
                    return readValue(source.get(property.getFieldName()), propType);
                }
            };
            EntityInstantiator instantiator = instantiators.getInstantiatorFor(entity);
            ParameterValueProvider<TarantoolPersistentProperty> provider = new PersistentEntityParameterValueProvider<>(entity, propertyValueProvider, null);
            Object instance = instantiator.createInstance(entity, provider);
            PersistentPropertyAccessor<?> propertyAccessor = new ConvertingPropertyAccessor<>(entity.getPropertyAccessor(instance), conversionService);
            entity.doWithProperties((PropertyHandler<TarantoolPersistentProperty>) property -> {
                if (entity.isConstructorArgument(property)) {
                    return;
                }
                Object propValue = propertyValueProvider.getPropertyValue(property);
                if (property.getType().isPrimitive() && propValue == null) {
                    return;
                }
                propertyAccessor.setProperty(property, propValue);;
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

            Map<Object, Object> converted = CollectionFactory.createMap(mapClass, keyClass, source.keySet().size());

            source.entrySet()
                    .forEach((e) -> {
                        Object key = (keyClass != null) ? conversionService.convert(e.getKey(), keyClass) : e.getKey();
                        Object value = readValue(e.getValue(), mapValueType);
                        converted.put(key, value);
                    });

            return (R) convertIfNeeded(converted, propertyType);
        }

        private Object convertIfNeeded(Object value, TypeInformation<?> propertyType) {
            Class<?> targetClass = propertyType.getType();
            if (targetClass.isAssignableFrom(value.getClass())) {
                return value;
            } else if (Enum.class.isAssignableFrom(targetClass)) {
                return Enum.valueOf((Class<Enum>) targetClass, value.toString());
            } else if (value instanceof Map && !conversionService.canConvert(value.getClass(), targetClass)) {
                return convertCustomType((Map<String, Object>) value, propertyType);
            }
            return conversionService.convert(value, targetClass);
        }
    }
}
