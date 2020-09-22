package org.springframework.data.tarantool.repository.support;

import org.springframework.data.repository.core.EntityInformation;

/**
 * Marker interface for the Tarantool entity information
 *
 * @param <T> domain object type
 * @param <ID> identifier type
 * @author Alexey Kuzin
 */
public interface TarantoolEntityInformation<T, ID> extends EntityInformation<T, ID> {
    /**
     * Returns the name of the Tarantool space the entity will be persisted to.
     *
     * @return not null String
     */
    String getSpaceName();

    /**
     * Returns the attribute that the id will be persisted to.
     *
     * @return not null String
     */
    String getIdAttribute();
}
