package org.springframework.data.tarantool.core.query;

import org.springframework.data.tarantool.core.TarantoolOperations;

/**
 * Helper for executing repository queries marked by {@link org.springframework.data.tarantool.repository.Query}
 * annotation and having result as object or list of objects
 *
 * @author Oleg Kuzentsov
 * @author Artyom Dubinin
 */
public class TarantoolObjectRepositoryQueryExecutor implements TarantoolRepositoryQueryExecutor {

    private final TarantoolOperations operations;
    private final TarantoolQueryMethod queryMethod;

    public TarantoolObjectRepositoryQueryExecutor(final TarantoolOperations operations,
                                                  final TarantoolQueryMethod queryMethod) {
        this.operations = operations;
        this.queryMethod = queryMethod;
    }

    @Override
    public Object execute(Object[] parameters) {
        final Class<?> returnedType = queryMethod.getResultProcessor().getReturnedType().getReturnedType();

        if (queryMethod.isCollectionQuery()) {
            return operations.callForObjectList(queryMethod.getQueryFunctionName(), parameters, returnedType);
        }
        return operations.callForObject(queryMethod.getQueryFunctionName(), parameters, returnedType);
    }
}
