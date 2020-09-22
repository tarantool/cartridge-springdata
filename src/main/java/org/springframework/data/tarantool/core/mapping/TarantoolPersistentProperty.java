package org.springframework.data.tarantool.core.mapping;

import org.springframework.data.mapping.PersistentProperty;

/**
 * Represents a property part of an entity that needs to be persisted.
 *
 * @author Alexey Kuzin
 */
public interface TarantoolPersistentProperty extends PersistentProperty<TarantoolPersistentProperty> {
    /**
     * Determine the field name from the {@link PersistentProperty} information
     * @return field name for looking up in a  source or writing to a sink
     */
    String getFieldName();
}
