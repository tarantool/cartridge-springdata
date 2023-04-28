package org.springframework.data.tarantool.repository;

import io.tarantool.driver.api.conditions.Conditions;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Tarantool specific {@link org.springframework.data.repository.Repository} interface.
 *
 * @author Alexey Kuzin
 * @author Ivan Dneprov
 */
@NoRepositoryBean
public interface TarantoolRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
    /**
     * Update all records selected by the specified conditions. The space is determined automatically by the
     * entity class. Warning: executing this operation on a large data set may cause OutOfMemory error or take
     * significant time to complete.
     *
     * @param query       tuple selection conditions
     * @param entity      entity with new data for update
     * @return list of updated objects
     */
    List<T> update(Conditions query, T entity);
}
