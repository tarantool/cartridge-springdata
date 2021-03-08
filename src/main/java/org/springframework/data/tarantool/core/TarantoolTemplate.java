package org.springframework.data.tarantool.core;

import io.tarantool.driver.api.SingleValueCallResult;
import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.conditions.Conditions;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import io.tarantool.driver.api.tuple.TarantoolTupleImpl;
import io.tarantool.driver.mappers.CallResultMapper;
import io.tarantool.driver.mappers.MessagePackMapper;
import io.tarantool.driver.mappers.MessagePackObjectMapper;
import io.tarantool.driver.mappers.ValueConverter;
import io.tarantool.driver.metadata.TarantoolSpaceMetadata;
import io.tarantool.driver.api.tuple.operations.TupleOperations;
import org.msgpack.value.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.tarantool.core.convert.TarantoolConverter;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentProperty;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TarantoolTemplate implements TarantoolOperations {

    private static final int MAX_WORKERS = 4;

    private final TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> tarantoolClient;
    private final TarantoolMappingContext mappingContext;
    private final TarantoolConverter converter;
    private final TarantoolExceptionTranslator exceptionTranslator;
    private final ForkJoinPool queryExecutors;
    private final MessagePackMapper mapper;

    public TarantoolTemplate(
            TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> tarantoolClient,
            TarantoolMappingContext mappingContext,
            TarantoolConverter converter,
            ForkJoinWorkerThreadFactory queryExecutorsFactory) {
        this.tarantoolClient = tarantoolClient;
        this.mappingContext = mappingContext;
        this.converter = converter;
        this.queryExecutors = new ForkJoinPool(
                Math.min(MAX_WORKERS, Runtime.getRuntime().availableProcessors()),
                queryExecutorsFactory, null, false
        );
        this.exceptionTranslator = new DefaultTarantoolExceptionTranslator();
        this.mapper = tarantoolClient.getConfig().getMessagePackMapper();
    }

    @Override
    public <T> T findOne(Conditions query, Class<T> entityClass) {
        Assert.notNull(query, "Query must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() ->
                tarantoolClient.space(entity.getSpaceName()).select(query)
        );
        return mapFirstToEntity(result, entityClass);
    }

    @Override
    public <T> List<T> find(Conditions query, Class<T> entityClass) {
        Assert.notNull(query, "Query must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() ->
                tarantoolClient.space(entity.getSpaceName()).select(query)
        );
        return result.stream().map(t -> mapToEntity(t, entityClass)).collect(Collectors.toList());
    }

    @Override
    public <T, ID> T findById(ID id, Class<T> entityClass) {
        Assert.notNull(id, "Id must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() -> {
            Conditions query = idQueryFromEntity(id).withLimit(1);
            return tarantoolClient.space(entity.getSpaceName()).select(query);
        });
        return mapFirstToEntity(result, entityClass);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        Assert.notNull(entityClass, "Entity class must not be null!");

        TarantoolPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() ->
                tarantoolClient.space(entity.getSpaceName()).select(Conditions.any())
        );
        return result.stream().map(t -> mapToEntity(t, entityClass)).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> findAndRemove(Conditions query, Class<T> entityType) {
        List<T> entities = find(query, entityType);
        return entities.stream().map(e -> remove(e, entityType)).collect(Collectors.toList());
    }

    @Override
    public <T> Long count(Conditions query, Class<T> entityType) {
        // not supported in the driver yet. TODO change this when implemented in the driver
        throw new NotImplementedException();
    }

    @Override
    public <T> T insert(T entity, Class<T> entityClass) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() ->
                tarantoolClient.space(entityMetadata.getSpaceName()).insert(mapToTuple(entity, entityMetadata))
        );
        return mapFirstToEntity(result, entityClass);
    }

    @Override
    public <T> T save(T entity, Class<T> entityClass) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entityClass, "Type must not be null!");

        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() ->
                tarantoolClient.space(entityMetadata.getSpaceName()).replace(mapToTuple(entity, entityMetadata))
        );
        return mapFirstToEntity(result, entityClass);
    }

    @Override
    public <T> List<T> update(Conditions query, T entity, Class<T> entityClass) {
        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolTuple newTuple = mapToTuple(entity, entityMetadata);
        return update(query, setNonNullFieldsFromTuple(newTuple), entityClass);
    }

    private TupleOperations setNonNullFieldsFromTuple(TarantoolTuple tuple) {
        final AtomicReference<TupleOperations> result = new AtomicReference<>();
        TupleOperations.fromTarantoolTuple(tuple).asList()
                .stream().filter(o -> !(o.getValue() instanceof io.tarantool.driver.api.tuple.TarantoolNullField))
                .forEach(op -> {
                    if (result.get() == null) {
                        result.set(TupleOperations.set(op.getFieldIndex(), op.getValue()));
                    } else {
                        result.get().addOperation(op);
                    }
                });
        return result.get();
    }

    private <T> List<T> update(Conditions query, TupleOperations updateOperations, Class<T> entityClass) {
        Assert.notNull(query, "Conditions must not be null!");
        Assert.notNull(updateOperations, "Update operations must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> tuplesForUpdate = executeSync(() ->
                tarantoolClient.space(entityMetadata.getSpaceName()).select(query)
        );
        List<CompletableFuture<TarantoolResult<TarantoolTuple>>> futures = tuplesForUpdate.stream()
                .map(tuple -> tarantoolClient.space(entityMetadata.getSpaceName())
                        .update(idQueryFromTuple(tuple, entityMetadata), updateOperations))
                .collect(Collectors.toList());
        return getFutureValue(queryExecutors.submit(
                () -> futures.stream().parallel()
                        .map(this::getFutureValue)
                        .map(tuples -> mapFirstToEntity(tuples, entityClass))
                        .collect(Collectors.toList())
        ));
    }

    @Override
    public <T> T remove(T entity, Class<T> entityClass) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        Conditions query = idQueryFromEntity(entity);
        return removeInternal(query, entityClass);
    }

    @Override
    public <T, ID> T removeById(ID id, Class<T> entityClass) {
        Assert.notNull(id, "ID must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        Conditions query = idQueryFromEntity(id);
        return removeInternal(query, entityClass);
    }

    @Override
    public <T> T call(String functionName, Object[] parameters, Class<T> entityType) {
        return call(functionName, Arrays.asList(parameters), entityType);
    }

    @Override
    public <T> T call(String functionName,
                      Object[] parameters,
                      Class<T> entityType,
                      ValueConverter<Value, T> entityConverter) {
        return call(functionName, Arrays.asList(parameters), entityType, entityConverter);
    }

    @Override
    public <T> T call(String functionName, List<?> parameters, Class<T> entityType) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityType, "Entity class must not be null!");

        List<T> result = callForList(functionName, parameters, entityType);
        return result != null && result.size() > 0 ? result.get(0) : null;
    }

    @Override
    public <T> T call(String functionName,
                      List<?> parameters,
                      Class<T> entityType,
                      ValueConverter<Value, T> entityConverter) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityType, "Entity class must not be null!");
        Assert.notNull(entityConverter, "Entity converter must not be null!");

        List<T> result = callForList(functionName, parameters, entityType, entityConverter);
        return result != null && result.size() > 0 ? result.get(0) : null;
    }

    @Override
    public <T> T call(String functionName, Class<T> entityType) {
        return call(functionName, Collections.emptyList(), entityType);
    }

    @Override
    public <T> T call(String functionName, Class<T> entityType, ValueConverter<Value, T> entityConverter) {
        return call(functionName, Collections.emptyList(), entityType, entityConverter);
    }

    @Override
    public <T> List<T> callForList(String functionName, Object[] parameters, Class<T> entityClass) {
        return callForList(functionName, Arrays.asList(parameters), entityClass);
    }

    @Override
    public <T> List<T> callForList(String functionName,
                                   Object[] parameters,
                                   Class<T> entityType,
                                   ValueConverter<Value, T> entityConverter) {
        return callForList(functionName, Arrays.asList(parameters), entityType, entityConverter);
    }

    @Override
    public <T> List<T> callForList(String functionName, List<?> parameters, Class<T> entityClass) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        return executeSync(getResultSupplier(
                functionName, parameters, tarantoolClient.getConfig().getMessagePackMapper(),
                entityClass, List.class)
        );
    }

    @Override
    public <T> List<T> callForList(String functionName,
                                   List<?> parameters,
                                   Class<T> entityClass,
                                   ValueConverter<Value, T> entityConverter) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");
        Assert.notNull(entityConverter, "Entity converter must not be null!");

        return executeSync(getCustomResultSupplier(
                functionName, parameters, tarantoolClient.getConfig().getMessagePackMapper(),
                List.class, entityConverter));
    }

    @Override
    public <T> List<T> callForList(String functionName, Class<T> entityType) {
        return callForList(functionName, Collections.emptyList(), entityType);
    }

    @Override
    public <T> List<T> callForList(String functionName, Class<T> entityType, ValueConverter<Value, T> entityConverter) {
        return callForList(functionName, Collections.emptyList(), entityType, entityConverter);
    }

    private List<?> mapParameters(List<?> parameters) {
        List<Object> mappedParameters = new LinkedList<>();
        getConverter().write(parameters, mappedParameters);
        return mappedParameters;
    }

    @SuppressWarnings("unchecked")
    private <T, R extends Collection<T>> Supplier<CompletableFuture<R>> getResultSupplier(
            String functionName,
            List<?> parameters,
            MessagePackObjectMapper parameterMapper,
            Class<T> entityClass,
            Class<R> entityCollectionClass) {
        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        if (entityMetadata.hasTupleAnnotation()) {
            return () -> tarantoolClient.call(functionName,
                    mapParameters(parameters),
                    tarantoolClient.getConfig().getMessagePackMapper(),
                    getResultMapperForEntity(entityClass))
                .thenApply(result -> result == null ? null : (R) result.stream()
                        .map(t -> mapToEntity(t, entityClass))
                        .collect(Collectors.toList())
                );
        } else {
            return getCustomResultSupplier(
                    functionName, parameters, parameterMapper, entityCollectionClass,
                    v -> mapToEntity(mapper.fromValue(v, Map.class), entityClass)
            );
        }
    }

    private <T, R extends Collection<T>> Supplier<CompletableFuture<R>> getCustomResultSupplier(
            String functionName,
            List<?> parameters,
            MessagePackObjectMapper parameterMapper,
            Class<R> entityCollectionClass,
            ValueConverter<Value, T> contentConverter) {
        return () -> tarantoolClient.callForSingleResult(
                functionName, mapParameters(parameters), parameterMapper,
                getCustomResultMapper(entityCollectionClass, contentConverter)
        );
    }

    @SuppressWarnings("unchecked")
    private
    <T, R extends Collection<T>> CallResultMapper<R, SingleValueCallResult<R>>
    getCustomResultMapper(Class<R> resultClass, ValueConverter<Value, T> contentConverter) {
        return tarantoolClient
                .getResultMapperFactoryFactory()
                .singleValueResultMapperFactory(resultClass)
                .withSingleValueResultConverter(v ->
                        (R) v.asArrayValue().list().stream()
                                .map(contentConverter::fromValue)
                                .collect(Collectors.toList()),
                        getCallResultClass(resultClass)
                );
    }

    private <T> Class<SingleValueCallResult<T>> getCallResultClass(Class<T> contentClass) {
        return (Class<SingleValueCallResult<T>>) (Class<?>) SingleValueCallResult.class;
    }

    private
    <T> CallResultMapper<TarantoolResult<TarantoolTuple>, SingleValueCallResult<TarantoolResult<TarantoolTuple>>>
    getResultMapperForEntity(Class<T> entityClass) {
        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        Optional<TarantoolSpaceMetadata> spaceMetadata = tarantoolClient.metadata()
                .getSpaceByName(entityMetadata.getSpaceName());
        return tarantoolClient
                .getResultMapperFactoryFactory()
                .defaultTupleSingleResultMapperFactory()
                .withDefaultTupleValueConverter(mapper, spaceMetadata.orElse(null));
    }

    @Nullable
    private <T> T removeInternal(Conditions query, Class<T> entityClass) {
        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() ->
                tarantoolClient.space(entityMetadata.getSpaceName()).delete(query)
        );
        return mapFirstToEntity(result, entityClass);
    }

    private Conditions idQueryFromTuple(TarantoolTuple tuple, TarantoolPersistentEntity<?> entity) {
        List<?> idValue = tuple.getFields();
        if (entity != null) {
            TarantoolPersistentProperty idProperty = entity.getIdProperty();
            if (idProperty == null) {
                throw new MappingException("No ID property specified in persistent entity " + entity.getType());
            }
            Object fieldValue = tuple.getObject(idProperty.getFieldName(), idProperty.getType())
                    .orElseThrow(() -> new MappingException("ID property value is null"));
            idValue = Collections.singletonList(fieldValue);
        }
        return Conditions.indexEquals(0, idValue);
    }

    private <T> Conditions idQueryFromEntity(T source) {
        TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(source.getClass());
        Object idValue = source;
        if (entity != null) {
            TarantoolPersistentProperty idProperty = entity.getIdProperty();
            if (idProperty == null) {
                throw new MappingException("No ID property specified on entity " + source.getClass());
            }

            PersistentPropertyAccessor<?> propertyAccessor = entity.getPropertyAccessor(source);
            idValue = propertyAccessor.getProperty(idProperty);
            if (idValue == null) {
                throw new MappingException("ID property value is null");
            }
        }
        Optional<Class<?>> basicTargetType = converter.getCustomConversions().getCustomWriteTarget(idValue.getClass());
        if (basicTargetType.isPresent()) {
            idValue = converter.getConversionService().convert(source, basicTargetType.get());
        }

        return Conditions.indexEquals(0, Collections.singletonList(idValue));
    }

    private <T> T mapFirstToEntity(TarantoolResult<TarantoolTuple> tuples, Class<T> entityClass) {
        return mapToEntity(tuples.stream()
                        .findFirst()
                        .orElse(null),
                entityClass);
    }

    private <T> T mapToEntity(@Nullable Object tuple, Class<T> entityClass) {
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
        return getFutureValue(func.get());
    }

    private <R> R getFutureValue(Future<R> future) {
        try {
            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                DataAccessException wrapped = exceptionTranslator
                        .translateExceptionIfPossible((RuntimeException) e.getCause());
                if (wrapped != null) {
                    throw wrapped;
                }
            }
            throw new DataRetrievalFailureException(e.getMessage(), e.getCause());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
