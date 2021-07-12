package org.springframework.data.tarantool.core.query;

import org.springframework.data.tarantool.core.TarantoolOperations;

/**
 * Helper for executing repository queries marked by {@link org.springframework.data.tarantool.repository.Query}
 * annotation and having result as tuple or list of tuples
 *
 * @author Alexey Kuzin
 */
public class TarantoolRepositoryTupleQueryExecutor implements TarantoolRepositoryExecutor {

    private final TarantoolOperations operations;
    private final TarantoolQueryMethod queryMethod;

    public TarantoolRepositoryTupleQueryExecutor(final TarantoolOperations operations,
                                                 final TarantoolQueryMethod queryMethod) {
        this.operations = operations;
        this.queryMethod = queryMethod;
    }

    /**
     * Execute the query
     *
     * @param parameters annotated method parameters
     * @return query result
     */
    public Object execute(final Object[] parameters) {
        final Class<?> domainClass = queryMethod.getResultProcessor().getReturnedType().getDomainType();

        String spaceName = queryMethod.getSpaceName()
                .orElse(operations.getMappingContext().getRequiredPersistentEntity(domainClass).getSpaceName());

        if (queryMethod.isCollectionQuery()) {
            return operations.callForTupleList(queryMethod.getQueryFunctionName(), parameters, spaceName, domainClass);
        } else {
            return operations.callForTuple(queryMethod.getQueryFunctionName(), parameters, spaceName, domainClass);
        }
    }
}
