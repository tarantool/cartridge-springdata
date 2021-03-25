package org.springframework.data.tarantool.core.mapping;

import io.tarantool.driver.exceptions.TarantoolException;
import lombok.SneakyThrows;
import org.springframework.data.mapping.IdentifierAccessor;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.TargetAwareIdentifierAccessor;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.tarantool.exceptions.TarantoolEntityOperationException;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Basic representation of a persistent entity
 *
 * @author Alexey Kuzin
 * @author Vladimir Rogach
 */
public class BasicTarantoolPersistentEntity<T>
        extends BasicPersistentEntity<T, TarantoolPersistentProperty>
        implements TarantoolPersistentEntity<T> {

    public BasicTarantoolPersistentEntity(TypeInformation<T> information) {
        super(information);
    }

    @Override
    public String getSpaceName() {
        Tuple annotationField = getType().getAnnotation(Tuple.class);

        if (annotationField != null && StringUtils.hasText(annotationField.value())) {
            return annotationField.value();
        }

        return getType().getSimpleName();
    }

    @Override
    public boolean hasTupleAnnotation() {
        return getType().getAnnotation(Tuple.class) != null;
    }

    @Override
    public boolean hasTarantoolIdClassAnnotation() {
        return getType().getAnnotation(TarantoolIdClass.class) != null;
    }

    @Override
    public Optional<Class<?>> getTarantoolIdClass() {
        if (!hasTarantoolIdClassAnnotation()) {
            return Optional.empty();
        }
        return Optional.of(getType().getAnnotation(TarantoolIdClass.class).value());
    }

    @Override
    protected TarantoolPersistentProperty returnPropertyIfBetterIdPropertyCandidateOrNull(TarantoolPersistentProperty property) {
        if (hasTarantoolIdClassAnnotation()) {
            return property.isIdProperty() ? property : null;
        }
        return super.returnPropertyIfBetterIdPropertyCandidateOrNull(property);
    }

    @Override
    public List<?> getCompositeIdParts(Object idValue) {
        List<Object> idParts = new LinkedList<>();
        for (Field field : idValue.getClass().getDeclaredFields()) {
            if(java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            TarantoolPersistentProperty property = getPersistentProperty(field.getName());
            Assert.notNull(property, "Property " + field.getName() + " does not exist in entity " + this);

            ReflectionUtils.makeAccessible(field);
            try {
                idParts.add(field.get(idValue));
            } catch (IllegalAccessException e) {
                throw new TarantoolEntityOperationException(getClass(), "Failed to get field " + field.getName(), e);
            }
        }
        return idParts;
    }

    @Override
    public IdentifierAccessor getIdentifierAccessor(Object bean) {
        if (hasTarantoolIdClassAnnotation()) {
            return new TarantoolCompositeIdentifierAccessor(this, bean);
        }
        return super.getIdentifierAccessor(bean);
    }

    private static class TarantoolCompositeIdentifierAccessor extends TargetAwareIdentifierAccessor {

        private final BasicTarantoolPersistentEntity<?> entity;
        private final Object bean;
        private final PersistentPropertyAccessor<Object> propertyAccessor;


        TarantoolCompositeIdentifierAccessor(BasicTarantoolPersistentEntity<?> entity, Object bean) {
            super(bean);
            this.entity = entity;
            this.bean = bean;
            this.propertyAccessor = entity.getPropertyAccessor(bean);
        }

        @Override
        public Object getIdentifier() {
            Optional<Class<?>> entityIdClass = entity.getTarantoolIdClass();

            //Maybe @TarantoolIdClass not set for entity.
            //Use getRequiredIdentifier to avoid getting nulls.
            if (!entityIdClass.isPresent()) {
                return null;
            }

            Class<?> idClass = entityIdClass.get();
            if (idClass.isInstance(bean)) {
                return bean;
            }

            Object id;
            try {
                id = Objects.requireNonNull(idClass).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new TarantoolEntityOperationException(entity.getName(), "Failed to create ID class instance", e);
            }

            for (Field field : idClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    TarantoolPersistentProperty property = entity.getPersistentProperty(field.getName());
                    Assert.notNull(property, "Property '"+ field.getName() + "' is null for " + entity);

                    Object value = propertyAccessor.getProperty(property);
                    ReflectionUtils.makeAccessible(field);
                    try {
                        field.set(id, value);
                    } catch (IllegalAccessException e) {
                        throw new TarantoolEntityOperationException(entity.getName(),
                                "Failed to set field " + field.getName(), e);
                    }
                }
            }
            return id;
        }
    }
}
