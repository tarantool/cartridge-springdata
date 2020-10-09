package org.springframework.data.tarantool.core.query;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.tarantool.core.TarantoolOperations;
import org.springframework.data.tarantool.repository.config.TarantoolRepositoryOperationsMapping;

import java.lang.reflect.Method;

/**
 * Strategy for looking up fluent API queries implementation
 *
 * @author Alexey Kuzin
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
        return new TarantoolRepositoryQuery(operations, queryMethod);
    }
}
