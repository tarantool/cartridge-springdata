package org.springframework.data.tarantool.repository.support;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.tarantool.core.TarantoolOperations;
import org.springframework.data.tarantool.repository.config.TarantoolRepositoryOperationsMapping;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.beans.factory.FactoryBean} implementation to ease container based configuration for
 * XML namespace and JavaConfig.
 *
 * @author Alexey Kuzin
 */
public class TarantoolRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends
        RepositoryFactoryBeanSupport<T, S, ID> {

    private TarantoolRepositoryOperationsMapping tarantoolOperationsMapping;

    /**
     * Creates a new {@link TarantoolRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public TarantoolRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * Set the template reference.
     *
     * @param operations the reference to the operations template.
     */
    public void setOperations(TarantoolOperations operations) {
        setTarantoolOperationsMapping(new TarantoolRepositoryOperationsMapping(operations));
    }

    public void setTarantoolOperationsMapping(final TarantoolRepositoryOperationsMapping mapping) {
        this.tarantoolOperationsMapping = mapping;
        setMappingContext(tarantoolOperationsMapping.getMappingContext());
    }

    @Override
    public void setMappingContext(MappingContext<?, ?> mappingContext) {
        super.setMappingContext(mappingContext);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new TarantoolRepositoryFactory(tarantoolOperationsMapping);
    }

    /**
     * Make sure that the dependencies are set and not null.
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(tarantoolOperationsMapping, "operationsMapping must not be null!");
    }
}
