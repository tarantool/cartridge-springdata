package org.springframework.data.tarantool.repository.support;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.tarantool.core.TarantoolOperations;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentProperty;
import org.springframework.data.tarantool.core.query.TarantoolQueryLookupStrategy;
import org.springframework.data.tarantool.repository.TarantoolRepository;
import org.springframework.data.tarantool.repository.config.TarantoolRepositoryOperationsMapping;

import java.util.Optional;

/**
 * Factory for {@link TarantoolRepository} instances.
 *
 * @author Alexey Kuzin
 */
public class TarantoolRepositoryFactory extends RepositoryFactorySupport {

    private final TarantoolRepositoryOperationsMapping operationsMapping;
    private final MappingContext<? extends TarantoolPersistentEntity<?>, TarantoolPersistentProperty> mappingContext;

    public TarantoolRepositoryFactory(TarantoolRepositoryOperationsMapping operationsMapping) {
        this.operationsMapping = operationsMapping;
        this.mappingContext = operationsMapping.defaultMappingContext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> TarantoolEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(domainClass);
        return new MappingTarantoolEntityInformation<>((TarantoolPersistentEntity<T>) entity);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
        TarantoolOperations tarantoolOperations = operationsMapping.resolve(
                repositoryInformation.getRepositoryInterface(), repositoryInformation.getDomainType());
        TarantoolEntityInformation<?, Object> entityInformation = getEntityInformation(repositoryInformation.getDomainType());
        return getTargetRepositoryViaReflection(repositoryInformation, entityInformation, tarantoolOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata repositoryMetadata) {
        return SimpleTarantoolRepository.class;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
            QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(new TarantoolQueryLookupStrategy(evaluationContextProvider, operationsMapping));
    }
}
