package org.springframework.data.tarantool.core.query;

import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.tarantool.core.TarantoolOperations;

/**
 * Represents query matched to a repository method
 *
 * @author Alexey Kuzin
 */
public class TarantoolRepositoryQuery implements RepositoryQuery {

    private final TarantoolOperations operations;
    private final TarantoolQueryMethod queryMethod;

    public TarantoolRepositoryQuery(final TarantoolOperations operations, final TarantoolQueryMethod queryMethod) {
        this.operations = operations;
        this.queryMethod = queryMethod;
    }

    @Override
    public Object execute(Object[] parameters) {
        return new TarantoolRepositoryQueryExecutor(operations, queryMethod).execute(parameters);
    }

    @Override
    public QueryMethod getQueryMethod() {
        return queryMethod;
    }
}
