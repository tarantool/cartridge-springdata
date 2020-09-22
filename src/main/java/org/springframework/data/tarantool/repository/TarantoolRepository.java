package org.springframework.data.tarantool.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Tarantool specific {@link org.springframework.data.repository.Repository} interface.
 *
 * @author Alexey Kuzin
 */
@NoRepositoryBean
public interface TarantoolRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
}
