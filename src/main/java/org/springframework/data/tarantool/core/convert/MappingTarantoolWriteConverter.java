package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.EntityWriter;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentProperty;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapping converter for Tarantool for writing objects to tuples
 *
 * @author Alexey Kuzin
 */
public class MappingTarantoolWriteConverter implements EntityWriter<Object, TarantoolTuple> {

    private final TarantoolTupleTypeMapper typeMapper;
    private final TarantoolMappingContext mappingContext;
    private final CustomConversions conversions;
    private final GenericConversionService conversionService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public MappingTarantoolWriteConverter(TarantoolTupleTypeMapper typeMapper,
                                          TarantoolMappingContext mappingContext,
                                          CustomConversions conversions,
                                          GenericConversionService conversionService) {

        this.typeMapper = typeMapper;
        this.mappingContext = mappingContext;
        this.conversions = conversions;
        this.conversionService = conversionService;
    }

    @Override
    public void write(Object source, TarantoolTuple target) {
        if (source == null) {
            return;
        }

        Optional<Class<?>> customTarget = conversions.getCustomWriteTarget(source.getClass(), target.getClass());

        if (customTarget.isPresent()) {
            TarantoolTuple result = conversionService.convert(source, TarantoolTuple.class);
            setFields(target, result);
            return;
        }

        TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(source.getClass());
        if (entity == null) {
            throw new MappingException("No mapping metadata found for entity ".concat(source.getClass().getName()));
        }
        ConvertingPropertyAccessor<?> accessor = new ConvertingPropertyAccessor<>(entity.getPropertyAccessor(source), conversionService);
        TarantoolPersistentProperty idProperty = entity.getIdProperty();
        if (idProperty != null && !target.getField(idProperty.getFieldName()).isPresent()) {
            try {
                Object id = accessor.getProperty(idProperty);
                target.putObject(idProperty.getFieldName(), id);
            } catch (ConversionException e) {
                logger.warn("Failed to convert id property '{}'. {}", idProperty.getFieldName(), e.getMessage());
            }
        }

        TypeInformation<?> type = ClassTypeInformation.from(source.getClass());
        addCustomTypeKeyIfNecessary(type, source, target);

        Map<String, Object> convertedProperties = convertProperties(entity, accessor);
        convertedProperties.forEach(target::putObject);
    }

    private void setFields(TarantoolTuple target, TarantoolTuple other) {
        target.getFields().clear();
        target.getFields().addAll(other.getFields());
    }

    /**
     * Adds custom type information to the given {@link TarantoolTuple} if necessary.
     *
     * @param source must not be {@literal null}.
     * @param target must not be {@literal null}.
     */
    private void addCustomTypeKeyIfNecessary(TypeInformation<?> type, Object source, TarantoolTuple target) {
        TypeInformation<?> actualType = type != null ? type.getActualType() : null;
        Class<?> reference = actualType == null ? Object.class : actualType.getType();
        Class<?> valueType = ClassUtils.getUserClass(source.getClass());

        if (!valueType.equals(reference)) {
            typeMapper.writeType(valueType, target);
        }
    }

    private Map<String, Object> convertProperties(TarantoolPersistentEntity<?> entity,
                                                  ConvertingPropertyAccessor<?> accessor) {
        Map<String, Object> target = new HashMap<>();

        entity.doWithProperties((PropertyHandler<TarantoolPersistentProperty>) property -> {
            Object value = accessor.getProperty(property);
            if (!property.isWritable()) {
                return;
            }
            Object valueToWrite = getValueToWrite(value, property.getTypeInformation());
            if (valueToWrite != null) {
                target.put(property.getFieldName(), valueToWrite);
            }
        });

        entity.doWithAssociations((AssociationHandler<TarantoolPersistentProperty>) association -> {
            TarantoolPersistentProperty inverse = association.getInverse();
            Object value = accessor.getProperty(inverse);
            Object valueToWrite = getValueToWrite(value, inverse.getTypeInformation());
            if (valueToWrite != null) {
                target.put(inverse.getFieldName(), valueToWrite);
            }
        });

        return target;
    }

    private Object getValueToWrite(Object value, TypeInformation<?> type) {
        if (value == null) {
            return null;
        } else if (type == null || conversions.isSimpleType(value.getClass())) {
            return getSimpleValueToWrite(value);
        } else {
            return getNonSimpleValueToWrite(value, type);
        }
    }

    private Object getSimpleValueToWrite(Object value) {
        Optional<Class<?>> customTarget = conversions.getCustomWriteTarget(value.getClass());
        return customTarget
                .map(aClass -> (Object) conversionService.convert(value, aClass))
                .orElse(value);
    }

    private Object getNonSimpleValueToWrite(Object value, TypeInformation<?> type) {
        TypeInformation<?> valueType = ClassTypeInformation.from(value.getClass());

        if (valueType.isCollectionLike()) {
            return convertCollection(asCollection(value), type);
        }

        if (valueType.isMap()) {
            return convertMap(asMap(value), type);
        }

        Optional<Class<?>> basicTargetType = conversions.getCustomWriteTarget(value.getClass());
        return basicTargetType
                .map(aClass -> (Object) conversionService.convert(value, aClass))
                .orElseGet(() -> convertCustomType(value, valueType));

    }

    private static Collection<?> asCollection(final Object source) {
        if (source instanceof Collection) {
            return (Collection<?>) source;
        }
        return source.getClass().isArray() ? CollectionUtils.arrayToList(source) : Collections.singleton(source);
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> asMap(Object value) {
        return (Map<Object, Object>) value;
    }

    private List<Object> convertCollection(final Collection<?> source, final TypeInformation<?> type) {
        Assert.notNull(source, "Given collection must not be null!");
        Assert.notNull(type, "Given type must not be null!");

        TypeInformation<?> componentType = type.getComponentType();

        return source.stream().map(element -> getValueToWrite(element, componentType)).collect(Collectors.toList());
    }

    private Map<String, Object> convertMap(final Map<Object, Object> source, final TypeInformation<?> type) {
        Assert.notNull(source, "Given map must not be null!");
        Assert.notNull(type, "Given type must not be null!");

        return source.entrySet().stream().collect(HashMap::new, (m, e) -> {
            Object key = e.getKey();
            Object value = e.getValue();
            if (!conversions.isSimpleType(key.getClass())) {
                throw new MappingException("Cannot use a complex object as a key value.");
            }
            String simpleKey = key.toString();
            Object convertedValue = getValueToWrite(value, type.getMapValueType());
            m.put(simpleKey, convertedValue);
        }, HashMap::putAll);
    }

    private Map<String, Object> convertCustomType(Object source, TypeInformation<?> type) {
        Assert.notNull(source, "Given map must not be null!");
        Assert.notNull(type, "Given type must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(source.getClass());
        ConvertingPropertyAccessor<?> accessor = new ConvertingPropertyAccessor<>(entity.getPropertyAccessor(source), conversionService);

        return convertProperties(entity, accessor);
    }
}
