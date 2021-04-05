package org.springframework.data.tarantool.core.mapping;

import org.springframework.data.mapping.PersistentEntity;

import java.util.List;
import java.util.Optional;

/**
 * Represents an entity to be persisted
 *
 * @param <T> domain object type
 * @author Alexey Kuzin
 */
public interface TarantoolPersistentEntity<T> extends PersistentEntity<T, TarantoolPersistentProperty> {
    /**
     * Tarantool space this entity can be saved to
     *
     * @return not null String
     */
    String getSpaceName();

    /**
     * Get information about {@link Tuple} annotation on the class
     *
     * @return true, if the {@link Tuple} annotation is set on the class
     */
    boolean hasTupleAnnotation();

    /**
     * Get information about {@link TarantoolIdClass} annotation on the class
     *
     * @return true if the {@link TarantoolIdClass} annotation is set on the class
     */
    boolean hasTarantoolIdClassAnnotation();

    /**
     * Get annotated identifier class type
     *
     * @return type specified in {@link TarantoolIdClass} or null if annotation was not specified
     */
    Optional<Class<?>> getTarantoolIdClass();

    /**
     * Extract property values from id object to list.
     *
     * @param idValue a bean of {@link TarantoolIdClass} type
     * @return list with property values
     */
    List<?> getCompositeIdParts(Object idValue);
}
