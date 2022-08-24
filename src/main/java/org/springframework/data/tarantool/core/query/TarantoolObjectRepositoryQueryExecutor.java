package org.springframework.data.tarantool.core.query;

import io.tarantool.driver.exceptions.TarantoolAccessDeniedException;
import io.tarantool.driver.exceptions.TarantoolClientException;
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
        if (operations.getMappingContext().hasPersistentEntityFor(returnedType)) {
            String spaceName = operations.getMappingContext().getRequiredPersistentEntity(returnedType).getSpaceName();
            try {
                if (queryMethod.isCollectionQuery()) {
                    return operations.callForObjectList(queryMethod.getQueryFunctionName(),
                            parameters, returnedType, spaceName);
                }
                return operations.callForObject(queryMethod.getQueryFunctionName(),
                        parameters, returnedType, spaceName);
            } catch (TarantoolClientException ex) {
                Throwable cause = ex.getCause();
                if (!(cause instanceof TarantoolAccessDeniedException)) {
                    throw ex;
                }
            }
        }

        if (queryMethod.isCollectionQuery()) {
            return operations.callForObjectList(queryMethod.getQueryFunctionName(), parameters, returnedType);
        }
        return operations.callForObject(queryMethod.getQueryFunctionName(), parameters, returnedType);
    }
}
