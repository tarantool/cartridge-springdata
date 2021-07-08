package org.springframework.data.tarantool.core.query;

import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.tarantool.core.TarantoolOperations;
import org.springframework.lang.Nullable;


/**
 * Represents query matched to a repository method that returns an object or list of objects
 *
 * @author Oleg Kuznetsov
 */
public class TarantoolObjectRepositoryQuery implements RepositoryQuery {

    private final TarantoolOperations operations;
    private final TarantoolQueryMethod queryMethod;

    public TarantoolObjectRepositoryQuery(TarantoolOperations operations, TarantoolQueryMethod queryMethod) {
        this.operations = operations;
        this.queryMethod = queryMethod;
    }

    @Nullable
    @Override
    public Object execute(Object[] parameters) {
        return new TarantoolRepositoryQueryObjectExecutor(operations, queryMethod)
                .execute(parameters);
    }

    @Override
    public QueryMethod getQueryMethod() {
        return queryMethod;
    }
}
