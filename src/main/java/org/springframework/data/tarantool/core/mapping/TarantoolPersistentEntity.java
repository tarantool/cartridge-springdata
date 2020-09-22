package org.springframework.data.tarantool.core.mapping;

import org.springframework.data.mapping.PersistentEntity;

/**
 * Represents an entity to be persisted
 *
 * @param <T> domain object type
 * @author Alexey Kuzin
 */
public interface TarantoolPersistentEntity<T> extends PersistentEntity<T, TarantoolPersistentProperty> {
    /**
     * Tarantool space this entity can be saved to
     * @return not null String
     */
    String getSpaceName();
}
