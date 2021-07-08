package org.springframework.data.tarantool.repository.config;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.tarantool.core.TarantoolOperations;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentProperty;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds mapping of {@link TarantoolOperations} to repositories and entities and allows to tell which implementation
 * must be used for the specific domain entity.
 *
 * @author Alexey Kuzin
 */
public class TarantoolRepositoryOperationsMapping {
    private TarantoolOperations defaultOperations;
    private Map<String, TarantoolOperations> byRepository = new HashMap<>();
    private Map<String, TarantoolOperations> byEntity = new HashMap<>();

    /**
     * Creates a new mapping, setting the default fallback to use by otherwise non mapped repositories.
     *
     * @param defaultOperations the default fallback template.
     */
    public TarantoolRepositoryOperationsMapping(TarantoolOperations defaultOperations) {
        this.defaultOperations = defaultOperations;
    }

    /**
     * Add a highest priority mapping that will associate a specific repository interface with a given
     * {@link TarantoolOperations}.
     *
     * @param repositoryInterface the repository interface {@link Class}.
     * @param tarantoolOperations the {@link TarantoolOperations} to use.
     */
    private void map(Class<?> repositoryInterface, TarantoolOperations tarantoolOperations) {
        byRepository.put(repositoryInterface.getName(), tarantoolOperations);
    }

    /**
     * Add a middle priority mapping that will associate any un-mapped repository that deals with the given domain type
     * Class with a given {@link TarantoolOperations}.
     *
     * @param entityClass         the domain type's {@link Class}.
     * @param tarantoolOperations the {@link TarantoolOperations} to use.
     */
    private void mapEntity(Class<?> entityClass, TarantoolOperations tarantoolOperations) {
        byEntity.put(entityClass.getName(), tarantoolOperations);
    }

    /**
     * Return the default {@link TarantoolOperations} implementation
     *
     * @return TarantoolOperations instance
     */
    public TarantoolOperations getDefaultOperations() {
        return defaultOperations;
    }

    /**
     * Return the default mapping context for using in repositories. It is taken from the the configured
     * default {@link TarantoolOperations} implementation
     *
     * @return mapping context
     */
    public MappingContext<? extends TarantoolPersistentEntity<?>, TarantoolPersistentProperty> defaultMappingContext() {
        return defaultOperations.getConverter().getMappingContext();
    }

    /**
     * Given a repository interface and its domain type, resolves which {@link TarantoolOperations} should be used
     * for them. The repository interface has the highest precedence and the entity class is lower. If no mapping is
     * found, the default implementation will be returned.
     *
     * @param repositoryInterface repository interface to search the mapping by
     * @param entityClass         domain entity class to search the mapping by
     * @return TarantoolOperations instance
     */
    public TarantoolOperations resolve(Class<?> repositoryInterface, Class<?> entityClass) {
        TarantoolOperations result = byRepository.get(repositoryInterface.getName());
        if (result == null) {
            result = byEntity.get(entityClass.getName());
        }
        if (result == null) {
            result = defaultOperations;
        }
        return result;
    }

    /**
     * Get the {@link MappingContext} to use in repositories. It is extracted from the default
     * {@link TarantoolOperations}.
     *
     * @return the mapping context.
     */
    public MappingContext<? extends TarantoolPersistentEntity<?>, TarantoolPersistentProperty> getMappingContext() {
        return defaultOperations.getConverter().getMappingContext();
    }

    /**
     * Builder for {@link TarantoolRepositoryOperationsMapping}
     *
     * @author Alexey Kuzin
     */
    public static class Builder {

        private TarantoolRepositoryOperationsMapping operationsMapping;

        /**
         * Basic constructor. Adds default fallback for operations mapping.
         *
         * @param defaultOperations default operations implementation
         */
        public Builder(TarantoolOperations defaultOperations) {
            Assert.notNull(defaultOperations, "The instance of TarantoolOperations must not be null");
            this.operationsMapping = new TarantoolRepositoryOperationsMapping(defaultOperations);
        }

        /**
         * Add a mapping of a specific repository interface to a {@link TarantoolOperations} implementation
         *
         * @param repositoryInterface the repository interface class
         * @param tarantoolOperations implementation for binding with
         * @return Builder instance
         */
        public Builder map(Class<?> repositoryInterface, TarantoolOperations tarantoolOperations) {
            this.operationsMapping.map(repositoryInterface, tarantoolOperations);
            return this;
        }

        /**
         * Add a mapping of a specific domain entity to a {@link TarantoolOperations} implementation
         *
         * @param entityClass         the domain entity class
         * @param tarantoolOperations implementation for binding with
         * @return Builder instance
         */
        public Builder mapEntity(Class<?> entityClass, TarantoolOperations tarantoolOperations) {
            this.operationsMapping.mapEntity(entityClass, tarantoolOperations);
            return this;
        }

        /**
         * Return the configured {@link TarantoolRepositoryOperationsMapping} instance
         *
         * @return operations mapping instance
         */
        public TarantoolRepositoryOperationsMapping build() {
            return operationsMapping;
        }
    }
}
