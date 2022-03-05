package org.springframework.data.tarantool.core.query;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.tarantool.core.TarantoolOperations;
import org.springframework.data.tarantool.repository.TarantoolSerializationType;
import org.springframework.data.tarantool.repository.config.TarantoolRepositoryOperationsMapping;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Strategy for looking up fluent API queries implementation
 *
 * @author Alexey Kuzin
 * @author Artyom Dubinin
 */
public class TarantoolQueryLookupStrategy implements QueryLookupStrategy {

    private final QueryMethodEvaluationContextProvider evaluationContextProvider;
    private final TarantoolRepositoryOperationsMapping operationsMapping;

    public TarantoolQueryLookupStrategy(final QueryMethodEvaluationContextProvider evaluationContextProvider,
                                        final TarantoolRepositoryOperationsMapping operationsMapping) {
        this.evaluationContextProvider = evaluationContextProvider;
        this.operationsMapping = operationsMapping;
    }

    @Override
    public RepositoryQuery resolveQuery(final Method method, final RepositoryMetadata metadata,
                                        final ProjectionFactory projectionFactory, final NamedQueries namedQueries) {
        final TarantoolOperations operations = operationsMapping.resolve(
                metadata.getRepositoryInterface(), metadata.getDomainType());

        TarantoolQueryMethod queryMethod = new TarantoolQueryMethod(method, metadata, projectionFactory);

        Optional<TarantoolSerializationType> output = queryMethod.getQueryOutputType();
        if (output.isPresent() && output.get().equals(TarantoolSerializationType.TUPLE)) {
            return new TarantoolTupleRepositoryQuery(operations, queryMethod);
        }
        return new TarantoolObjectRepositoryQuery(operations, queryMethod);
    }
}
