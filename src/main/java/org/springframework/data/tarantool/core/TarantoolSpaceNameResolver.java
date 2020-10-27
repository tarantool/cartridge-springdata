package org.springframework.data.tarantool.core;

/**
 * Allows to determine the target space from entity properties
 *
 * @author Alexey Kuzin
 */
public interface TarantoolSpaceNameResolver {
    /**
     * Taste a class and determine the space name for it
     * @param entityType tuple entity class
     * @param <T> entity type
     * @return space name
     */
    <T> String getSpaceNameForEntity(Class<T> entityType);
}
