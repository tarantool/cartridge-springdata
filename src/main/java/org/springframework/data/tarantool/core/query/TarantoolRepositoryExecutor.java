package org.springframework.data.tarantool.core.query;

/**
 * Helpers interface for executing repository queries
 *
 * @author Oleg Kuznetsov
 */
public interface TarantoolRepositoryExecutor {

    Object execute(final Object[] parameters);
}
