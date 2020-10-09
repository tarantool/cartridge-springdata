package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.EntityInstantiator;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.mapping.model.PersistentEntityParameterValueProvider;
import org.springframework.data.mapping.model.PropertyValueProvider;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentProperty;
import org.springframework.data.tarantool.core.query.support.IndexDefinition;
import org.springframework.data.tarantool.core.query.support.IndexKeyDefinition;
import org.springframework.data.tarantool.core.query.support.Query;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.Optional;

/**
 * A mapping converter for Tarantool.
 *
 * @author Alexey Kuzin
 */
public class MappingTarantoolConverter extends AbstractTarantoolConverter implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TarantoolMappingContext mappingContext;
    private final DefaultTarantoolTypeMapper typeMapper;

    private ApplicationContext applicationContext;
    /*
     * Callbacks for Audit Mechanism
     */
    private @Nullable EntityCallbacks entityCallbacks;

    public MappingTarantoolConverter(TarantoolMappingContext mappingContext,
                                     CustomConversions conversions) {
        super(conversions);
        this.mappingContext = mappingContext;
        this.typeMapper = new DefaultTarantoolTypeMapper();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        if (entityCallbacks == null) {
            setEntityCallbacks(EntityCallbacks.create(applicationContext));
        }
    }

    /**
     * Set the {@link EntityCallbacks} instance. Overrides potentially existing {@link EntityCallbacks}.
     *
     * @param entityCallbacks must not be {@literal null}.
     * @throws IllegalArgumentException if the given instance is {@literal null}.
     */
    public void setEntityCallbacks(EntityCallbacks entityCallbacks) {
        Assert.notNull(entityCallbacks, "EntityCallbacks must not be null");
        this.entityCallbacks = entityCallbacks;
    }

    @Override
    public <R> R read(final Class<R> clazz, TarantoolTuple source) {
        return read(ClassTypeInformation.from(clazz), source);
    }

    @SuppressWarnings("unchecked")
    public <R> R read(TypeInformation<R> resultType, @Nullable TarantoolTuple source) {
        if (source == null) {
            return null;
        }

        TypeInformation<? extends R> typeToUse = typeMapper.readType(source, resultType);
        Class<? extends R> rawType = typeToUse.getType();

        if (conversions.hasCustomReadTarget(source.getClass(), rawType)) {
            return conversionService.convert(source, rawType);
        }

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(typeToUse);

        ParameterValueProvider<TarantoolPersistentProperty> provider = getParameterProvider(entity, source);
        EntityInstantiator instantiator = instantiators.getInstantiatorFor(entity);

        Object instance = instantiator.createInstance(entity, provider);
        ConvertingPropertyAccessor propertyAccessor = getPropertyAccessor(instance);

        entity.doWithProperties((PropertyHandler<TarantoolPersistentProperty>) property -> {
            if (!source.getField(property.getFieldName()).isPresent() || entity.isConstructorArgument(property)) {
                return;
            }

            Object propValue = getValueInternal(source, property, instance);
            propertyAccessor.setProperty(property, propValue);
        });

        entity.doWithAssociations((AssociationHandler<TarantoolPersistentProperty>) association -> {
            TarantoolPersistentProperty inverseProperty = association.getInverse();
            Object propValue = getValueInternal(source, inverseProperty, instance);
            propertyAccessor.setProperty(inverseProperty, propValue);
        });

        return (R) propertyAccessor.getBean();
    }

    private Object getValueInternal(final @Nullable TarantoolTuple source, final TarantoolPersistentProperty property, final Object parent) {
        return getPropertyValueProvider(source).getPropertyValue(property);
    }

    private ConvertingPropertyAccessor<?> getPropertyAccessor(Object instance) {
        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(instance.getClass());
        PersistentPropertyAccessor<?> accessor = entity.getPropertyAccessor(instance);

        return new ConvertingPropertyAccessor<>(accessor, conversionService);
    }

    private ParameterValueProvider<TarantoolPersistentProperty> getParameterProvider(
            TarantoolPersistentEntity<?> entity, TarantoolTuple source) {
        return new PersistentEntityParameterValueProvider<>(entity, getPropertyValueProvider(source), null);
    }

    @Override
    public void write(Object source, Object target) {

        if (source == null) {
            return;
        }

        if (target instanceof Query) {
            writeIndexQueryInternal(source, (Query) target);
        } else {
            writeTupleInternal(source, (TarantoolTuple) target);
        }
    }

    private void writeIndexQueryInternal(Object source, Query target) {
        TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(source.getClass());
        Object idValue = source;
        if (entity != null) {
            PersistentPropertyAccessor<?> propertyAccessor = entity.getPropertyAccessor(source);
            TarantoolPersistentProperty idProperty = entity.getIdProperty();
            if (idProperty == null) {
                throw new MappingException("No ID property specified on entity " + source.getClass());
            }

            idValue = propertyAccessor.getProperty(idProperty);
        }
        Optional<Class<?>> basicTargetType = conversions.getCustomWriteTarget(idValue.getClass());
        if (basicTargetType.isPresent()) {
            idValue = conversionService.convert(source, basicTargetType.get());
        }
        IndexKeyDefinition indexKeyDef = new IndexKeyDefinition(idValue);
        target.setIndexDefinition(new IndexDefinition(IndexDefinition.PRIMARY, Collections.singletonList(indexKeyDef)));
    }

    /**
     * Convert a source object into a {@link TarantoolTuple} target.
     *
     * @param source the source object.
     * @param target the target tuple.
     */
    @SuppressWarnings("unchecked")
    private void writeTupleInternal(final Object source, TarantoolTuple target) {

        Optional<Class<?>> customTarget = conversions.getCustomWriteTarget(source.getClass(), target.getClass());

        if (customTarget.isPresent()) {
            TarantoolTuple result = conversionService.convert(source, TarantoolTuple.class);
            setFields(target, result);
            return;
        }

        TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(source.getClass());
        writeTupleInternal(source, target, entity);

        addCustomTypeKeyIfNecessary(source, target);
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
    private void addCustomTypeKeyIfNecessary(Object source, TarantoolTuple target) {

        TypeInformation<?> type = ClassTypeInformation.from(source.getClass());
        TypeInformation<?> actualType = type != null ? type.getActualType() : null;
        Class<?> reference = actualType == null ? Object.class : actualType.getType();
        Class<?> valueType = ClassUtils.getUserClass(source.getClass());

        if (!valueType.equals(reference)) {
            typeMapper.writeType(valueType, target);
        }
    }

    /**
     * Internal helper method to write the source object into the target tuple.
     *
     * @param source the source object.
     * @param target the target tuple.
     * @param entity the persistent entity to convert from.
     */
    @SuppressWarnings("unchecked")
    private void writeTupleInternal(final Object source, final TarantoolTuple target, final TarantoolPersistentEntity<?> entity) {

        if (entity == null) {
            throw new MappingException("No mapping metadata found for entity ".concat(source.getClass().getName()));
        }

        final PersistentPropertyAccessor propertyAccessor = entity.getPropertyAccessor(source);
        final TarantoolPersistentProperty idProperty = entity.getIdProperty();

        if (idProperty != null && !target.getField(idProperty.getFieldName()).isPresent()) {
            try {
                Object id = propertyAccessor.getProperty(idProperty);
                target.putObject(idProperty.getFieldName(), id);
            } catch (ConversionException e) {
                logger.warn("Failed to convert id property '{}'. {}", new Object[]{idProperty.getFieldName(),
                        e.getMessage()});
            }
        }

        entity.doWithProperties(new PropertyHandler<TarantoolPersistentProperty>() {

            @Override
            public void doWithPersistentProperty(TarantoolPersistentProperty property) {
                if (property.equals(idProperty)) {
                    return;
                }

                Object propertyObj = propertyAccessor.getProperty(property);
                if (propertyObj != null) {
                    if (!conversions.isSimpleType(propertyObj.getClass())) {
                        writePropertyInternal(propertyObj, target, property);
                    } else {
                        writeSimpleInternal(propertyObj, target, property.getFieldName());
                    }
                }
            }
        });

        entity.doWithAssociations(new AssociationHandler<TarantoolPersistentProperty>() {
            @Override
            public void doWithAssociation(final Association<TarantoolPersistentProperty> association) {
                TarantoolPersistentProperty inverse = association.getInverse();
                Object propertyObj = propertyAccessor.getProperty(inverse);
                if (propertyObj != null) {
                    writePropertyInternal(propertyObj, target, inverse);
                }
            }
        });
    }

    /**
     * Writes the given simple value to the given {@link TarantoolTuple}. Will store enum names for enum values.
     *
     * @param source the value to write
     * @param target must not be {@literal null}.
     * @param key must not be {@literal null}.
     */
    private void writeSimpleInternal(final Object source, final TarantoolTuple target, final String key) {
        target.putObject(key, potentiallyConvertValueForWrite(source));
    }

    /**
     * Checks whether we have a custom conversion registered for the given value into an arbitrary simple type.
     * Returns the converted value if so. If not, we perform special enum handling or simply return the value as is.
     *
     * @param value value to convert
     * @return converted object
     */
    private Object potentiallyConvertValueForWrite(Object value) {
        Optional<Class<?>> customTarget = conversions.getCustomWriteTarget(value.getClass());

        if(customTarget.isPresent()) {
            return conversionService.convert(value, customTarget.get());
        } else {
            return Enum.class.isAssignableFrom(value.getClass()) ? ((Enum<?>) value).toString() : value;
        }
    }

    /**
     * Helper method to write a property into the target Tarantool tuple.
     *
     * @param source the source object.
     * @param target the target tuple.
     * @param property the property information.
     */
    @SuppressWarnings("unchecked")
    private void writePropertyInternal(final Object source, final TarantoolTuple target, final TarantoolPersistentProperty property) {

        if (source == null) {
            return;
        }

        String name = property.getFieldName();

        Optional<Class<?>> basicTargetType = conversions.getCustomWriteTarget(source.getClass());
        if (basicTargetType.isPresent()) {
            target.putObject(name, conversionService.convert(source, basicTargetType.get()));
            return;
        }

        target.putObject(name, source);
    }

    private PropertyValueProvider<TarantoolPersistentProperty> getPropertyValueProvider(TarantoolTuple source) {
        return new TarantoolPropertyValueProvider(source, conversions, conversionService);
    }

    @Override
    public TarantoolMappingContext getMappingContext() {
        return (TarantoolMappingContext) mappingContext;
    }

    /**
     * A {@link PropertyValueProvider} to get field values from a tuple
     *
     * @author Alexey Kuzin
     */
    private static class TarantoolPropertyValueProvider implements PropertyValueProvider<TarantoolPersistentProperty> {

        private TarantoolTuple source;
        private CustomConversions conversions;
        private ConversionService conversionService;

        public TarantoolPropertyValueProvider(TarantoolTuple source,
                                              CustomConversions conversions,
                                              ConversionService conversionService) {
            this.source = source;
            this.conversions = conversions;
            this.conversionService = conversionService;
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
            if (conversions.isSimpleType(propClass)) {
                return (R) source.getObject(property.getFieldName(), propClass).orElse(null);
            }
            if (conversions.hasCustomReadTarget(String.class, propClass)) {
                return (R) conversionService.convert(source.getString(property.getFieldName()), propClass);
            } else {
                if (Enum.class.isAssignableFrom(propClass)) {
                    return (R) Enum.valueOf((Class<Enum>) propClass, source.getString(property.getFieldName()));
                }

                if (Class.class.isAssignableFrom(propClass)) {
                    try {
                        return (R) Class.forName(source.getString(property.getFieldName()));
                    } catch (ClassNotFoundException e) {
                        throw new MappingException(
                                "Unable to create class from " + source.getString(property.getFieldName()));
                    }
                }

                Object value = source.getObject(source.getString(property.getFieldName()), Object.class).orElse(null);

                return value == null || propClass.isAssignableFrom(value.getClass()) ?
                        (R) value : (R) conversionService.convert(value, propClass);
            }
        }
    }
}
