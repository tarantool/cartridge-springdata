package org.springframework.data.tarantool.core.query;

public interface TarantoolRepositoryExecutor {

    Object execute(final Object[] parameters);
}
