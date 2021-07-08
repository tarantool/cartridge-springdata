package org.springframework.data.tarantool.core.query;

import org.springframework.data.tarantool.core.TarantoolOperations;

public class TarantoolRepositoryQueryObjectExecutor implements TarantoolRepositoryExecutor {

    private final TarantoolOperations operations;
    private final TarantoolQueryMethod queryMethod;

    public TarantoolRepositoryQueryObjectExecutor(final TarantoolOperations operations,
                                                  final TarantoolQueryMethod queryMethod) {
        this.operations = operations;
        this.queryMethod = queryMethod;
    }

    @Override
    public Object execute(Object[] parameters) {
        final Class<?> domainClass = queryMethod.getResultProcessor().getReturnedType().getDomainType();

        if (queryMethod.isCollectionQuery()) {
            return operations.callForObjectList(queryMethod.getQueryFunctionName(), parameters, domainClass);
        } else {
            return operations.callForObject(queryMethod.getQueryFunctionName(), parameters, domainClass);
        }
    }
}
