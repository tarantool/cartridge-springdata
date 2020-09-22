package org.springframework.data.tarantool.core;

import io.tarantool.driver.TarantoolClient;
import io.tarantool.driver.api.TarantoolIndexQuery;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.TarantoolSelectOptions;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import io.tarantool.driver.api.tuple.TarantoolTupleImpl;
import io.tarantool.driver.exceptions.TarantoolException;
import io.tarantool.driver.metadata.TarantoolSpaceMetadata;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.tarantool.core.convert.TarantoolConverter;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TarantoolTemplate implements TarantoolOperations {

    private TarantoolClient tarantoolClient;
    private TarantoolMappingContext mappingContext;
    private TarantoolConverter converter;
    private TarantoolExceptionTranslator exceptionTranslator;

    public TarantoolTemplate(TarantoolClient tarantoolClient,
                             TarantoolMappingContext mappingContext,
                             TarantoolConverter converter) {
        this.tarantoolClient = tarantoolClient;
        this.mappingContext = mappingContext;
        this.converter = converter;
        this.exceptionTranslator = new DefaultTarantoolExceptionTranslator();
    }

    @Override
    public <T> T findOne(Query query, Class<T> entityClass) {
        Assert.notNull(query, "Query must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() -> {
            try {
                TarantoolSelectOptions options = new TarantoolSelectOptions.Builder().build();
                return tarantoolClient
                        .space(entity.getSpaceName())
                        .select(query.toIndexQuery(), options);
            } catch (TarantoolException e) {
                throw exceptionTranslator.translateExceptionIfPossible(e);
            }
        });
        return mapFirstToEntity(result, entityClass);
    }

    @Override
    public <T> List<T> find(Query query, Class<T> entityClass) {
        Assert.notNull(query, "Query must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() -> {
            try {
                TarantoolSelectOptions options = new TarantoolSelectOptions.Builder().build();
                return tarantoolClient
                        .space(entity.getSpaceName())
                        .select(query.toIndexQuery(), options);
            } catch (TarantoolException e) {
                throw exceptionTranslator.translateExceptionIfPossible(e);
            }
        });
        return result.stream().map(t -> mapToEntity(t, entityClass)).collect(Collectors.toList());
    }

    @Override
    public <T, ID> T findById(ID id, Class<T> entityClass) {
        Assert.notNull(id, "Id must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() -> {
            try {
                TarantoolIndexQuery query = idQueryFromEntity(id).toIndexQuery();
                TarantoolSelectOptions options = new TarantoolSelectOptions.Builder().withLimit(1).build();
                return tarantoolClient
                        .space(entity.getSpaceName())
                        .select(query, options);
            } catch (TarantoolException e) {
                throw exceptionTranslator.translateExceptionIfPossible(e);
            }
        });
        return mapFirstToEntity(result, entityClass);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() -> {
            try {
                TarantoolSelectOptions options = new TarantoolSelectOptions.Builder().build();
                return tarantoolClient
                        .space(entity.getSpaceName())
                        .select(options);
            } catch (TarantoolException e) {
                throw exceptionTranslator.translateExceptionIfPossible(e);
            }
        });
        return result.stream().map(t -> mapToEntity(t, entityClass)).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> findAndRemove(Query query, Class<T> entityType) {
        List<T> entities = find(query, entityType);
        return entities.stream().map(e -> remove(e, entityType)).collect(Collectors.toList());
    }

    @Override
    public <T> Long count(Query query, Class<T> entityType) {
        // not supported in the driver yet. TODO change this when implemented in the driver
        throw new NotImplementedException();
    }

    @Override
    public <T> T insert(T entity, Class<T> entityClass) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entity.getClass());
        TarantoolResult<TarantoolTuple> result = executeSync(() -> {
            try {
                return tarantoolClient
                        .space(entityMetadata.getSpaceName())
                        .insert(mapToTuple(entity, entityMetadata));
            } catch (TarantoolException e) {
                throw exceptionTranslator.translateExceptionIfPossible(e);
            }
        });
        return mapFirstToEntity(result, entityClass);
    }

    @Override
    public <T> T save(T entity, Class<T> entityClass) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entity.getClass());
        TarantoolResult<TarantoolTuple> result = executeSync(() -> {
            try {
                return tarantoolClient
                        .space(entityMetadata.getSpaceName())
                        .replace(mapToTuple(entity, entityMetadata));
            } catch (TarantoolException e) {
                throw exceptionTranslator.translateExceptionIfPossible(e);
            }
        });
        return mapFirstToEntity(result, entityClass);
    }

    @Override
    public <T> T remove(T entity, Class<T> entityClass) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolIndexQuery query = idQueryFromEntity(entity).toIndexQuery();
        return removeInternal(query, entityClass);
    }

    @Override
    public <T, ID> T removeById(ID id, Class<T> entityClass) {
        Assert.notNull(id, "ID must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolIndexQuery query = idQueryFromEntity(id).toIndexQuery();
        return removeInternal(query, entityClass);
    }

    private <T> T removeInternal(TarantoolIndexQuery query, Class<T> entityClass) {
        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() -> {
            try {
                return tarantoolClient
                        .space(entityMetadata.getSpaceName())
                        .delete(query);
            } catch (TarantoolException e) {
                throw exceptionTranslator.translateExceptionIfPossible(e);
            }
        });
        return mapFirstToEntity(result, entityClass);
    }

    private <T> Query idQueryFromEntity(T entity) {
        Query query = new Query();
        getConverter().write(entity, query);
        return query;
    }

    @Nullable
    private <T> T mapFirstToEntity(TarantoolResult<TarantoolTuple> tuples, Class<T> entityClass) {
        return mapToEntity(tuples.stream()
                    .findFirst()
                    .orElse(null),
                entityClass);
    }

    private <T> T mapToEntity(TarantoolTuple tuple, Class<T> entityClass) {
        return getConverter().read(entityClass, tuple);
    }

    private <T> TarantoolTuple mapToTuple(T entity, TarantoolPersistentEntity<?> entityMetadata) {
        Optional<TarantoolSpaceMetadata> spaceMetadata = tarantoolClient.metadata()
                .getSpaceByName(entityMetadata.getSpaceName());
        TarantoolTuple tuple = spaceMetadata.isPresent() ?
                new TarantoolTupleImpl(tarantoolClient.getConfig().getMessagePackMapper(), spaceMetadata.get()) :
                new TarantoolTupleImpl(tarantoolClient.getConfig().getMessagePackMapper());
        getConverter().write(entity, tuple);
        return tuple;
    }

    @Override
    public TarantoolConverter getConverter() {
        return converter;
    }

    private <R> R executeSync(Supplier<CompletableFuture<R>> func) {
        try {
            return func.get().get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DataRetrievalFailureException(e.getMessage());
        }
    }
}
