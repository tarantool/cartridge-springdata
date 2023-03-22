package org.springframework.data.tarantool.core;

import io.tarantool.driver.api.SingleValueCallResult;
import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.conditions.Conditions;
import io.tarantool.driver.api.metadata.TarantoolSpaceMetadata;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import io.tarantool.driver.api.tuple.operations.TupleOperations;
import io.tarantool.driver.core.tuple.TarantoolTupleImpl;
import io.tarantool.driver.mappers.CallResultMapper;
import io.tarantool.driver.mappers.DefaultMessagePackMapper;
import io.tarantool.driver.mappers.MessagePackMapper;
import io.tarantool.driver.mappers.MessagePackObjectMapper;
import io.tarantool.driver.mappers.converters.ValueConverter;
import io.tarantool.driver.mappers.factories.DefaultMessagePackMapperFactory;
import io.tarantool.driver.mappers.factories.ResultMapperFactoryFactory;
import io.tarantool.driver.mappers.factories.ResultMapperFactoryFactoryImpl;
import io.tarantool.driver.protocol.TarantoolIndexQuery;
import org.msgpack.value.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.tarantool.core.convert.TarantoolConverter;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.exceptions.TarantoolMetadataMissingException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.springframework.data.tarantool.core.TarantoolTemplateUtils.getIndexPartValues;
import static org.springframework.data.tarantool.core.TarantoolTemplateUtils.idQueryFromTuple;

/**
 * This class contains base CRUD operations for Tarantool instance
 *
 * @author Alexey Kuzin
 * @author Oleg Kuznetsov
 * @author Artyom Dubinin
 */
abstract class BaseTarantoolTemplate implements TarantoolOperations {

    protected static final int MAX_WORKERS = 4;

    protected final TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> tarantoolClient;
    protected final TarantoolMappingContext mappingContext;
    protected final TarantoolConverter converter;
    protected final TarantoolExceptionTranslator exceptionTranslator;
    protected final ForkJoinPool queryExecutors;
    protected final MessagePackMapper mapper;
    protected final ResultMapperFactoryFactory mapperFactoryFactory;

    BaseTarantoolTemplate(
            TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> tarantoolClient,
            TarantoolMappingContext mappingContext,
            TarantoolConverter converter,
            ForkJoinPool.ForkJoinWorkerThreadFactory queryExecutorsFactory) {
        this.tarantoolClient = tarantoolClient;
        this.mappingContext = mappingContext;
        this.converter = converter;
        this.queryExecutors = new ForkJoinPool(
                Math.min(MAX_WORKERS, Runtime.getRuntime().availableProcessors()),
                queryExecutorsFactory, null, false
        );
        this.exceptionTranslator = new DefaultTarantoolExceptionTranslator();
        this.mapper = tarantoolClient.getConfig().getMessagePackMapper();
        this.mapperFactoryFactory = new ResultMapperFactoryFactoryImpl();
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
            Conditions query = idQueryFromObject(id, entityClass).withLimit(1);
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

        throw new IllegalStateException("not implemented");
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

        Conditions query = idQueryFromObject(id, entityClass);
        return removeInternal(query, entityClass);
    }

    @Override
    public void truncate(String spaceName) {
        executeSync(() -> tarantoolClient.space(spaceName).truncate());
    }

    @Override
    public TarantoolConverter getConverter() {
        return converter;
    }

    @Override
    public TarantoolMappingContext getMappingContext() {
        return mappingContext;
    }

    /**
     * Build conditions query from entity object that contains id.
     * This object can be:
     * - the entity object (Book, Employee etc)
     *
     * @param source the entity object
     * @param <T>    target entity type
     * @return condition for this id object
     */
    protected <T> Conditions idQueryFromEntity(T source) {
        TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(source.getClass());
        Assert.notNull(entity, "Failed to get entity class for " + source.getClass());

        Object idValue = entity.getIdentifierAccessor(source).getRequiredIdentifier();

        List<?> indexPartValues = getIndexPartValues(idValue, entity);
        return createIndexEqualsConditionFromParts(indexPartValues);
    }

    /**
     * Build conditions query from object that contains id.
     * This object can be:
     * - the basic type representing id of an entity (Integer, String,  etc)
     * - the 'TarantoolIdClass' object if entity has composite ID
     *
     * @param source      the id object
     * @param entityClass class of entity
     * @param <T>         target entity type
     * @return condition for this id object
     */
    protected <T> Conditions idQueryFromObject(T source, Class<?> entityClass) {
        TarantoolPersistentEntity<?> entity = mappingContext.getPersistentEntity(entityClass);
        Assert.notNull(entity, "Failed to get entity class for " + entityClass +
                ". Possibly @Tuple annotation is missing on the class.");

        List<?> indexPartValues = getIndexPartValues(source, entity);
        return createIndexEqualsConditionFromParts(indexPartValues);
    }

    protected <T> List<T> update(Conditions query, TupleOperations updateOperations, Class<T> entityClass) {
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

    protected TupleOperations setNonNullFieldsFromTuple(TarantoolTuple tuple) {
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

    @Nullable
    protected <T> T removeInternal(Conditions query, Class<T> entityClass) {
        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        TarantoolResult<TarantoolTuple> result = executeSync(() ->
                tarantoolClient.space(entityMetadata.getSpaceName()).delete(query)
        );
        return mapFirstToEntity(result, entityClass);
    }

    protected <T> TarantoolTuple mapToTuple(T entity, TarantoolPersistentEntity<?> entityMetadata) {
        Optional<TarantoolSpaceMetadata> spaceMetadata = tarantoolClient.metadata()
                .getSpaceByName(entityMetadata.getSpaceName());
        TarantoolTuple tuple = spaceMetadata.isPresent() ?
                new TarantoolTupleImpl(getMessagePackMapper(), spaceMetadata.get()) :
                new TarantoolTupleImpl(getMessagePackMapper());
        getConverter().write(entity, tuple);
        return tuple;
    }

    protected <R> R executeSync(Supplier<CompletableFuture<R>> func) {
        return getFutureValue(func.get());
    }

    protected <R> R getFutureValue(Future<R> future) {
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

    @SuppressWarnings("unchecked")
    protected <T, R extends List<T>> Supplier<CompletableFuture<R>> getResultSupplier(
            String functionName,
            List<?> parameters,
            String spaceName,
            Class<T> entityClass) {
        return () -> tarantoolClient.call(functionName,
                mapParameters(parameters),
                getMessagePackMapper(),
                getResultMapperForEntity(spaceName, entityClass))
                .thenApply(result -> result == null ? null : (R) result.stream()
                        .map(t -> mapToEntity(t, entityClass))
                        .collect(Collectors.toList())
                );
    }

    @SuppressWarnings("unchecked")
    protected <T, R extends List<T>> Supplier<CompletableFuture<R>> getCustomResultSupplier(
            String functionName,
            List<?> parameters,
            MessagePackObjectMapper parameterMapper,
            ValueConverter<Value, T> contentConverter) {
        ValueConverter<Value, R> converter = v -> v.isNilValue() ? null
                : (R) v.asArrayValue().list().stream()
                .map(contentConverter::fromValue)
                .collect(Collectors.toList());

        return () -> tarantoolClient.callForSingleResult(
                functionName, mapParameters(parameters), parameterMapper, converter
        );
    }

    protected <T> ValueConverter<Value, List<T>> getListValueConverter(Class<T> entityClass) {
        return result -> result == null ? null
                : result.asArrayValue().list().stream()
                .map(v -> mapToEntity(mapper.fromValue(v, Map.class), entityClass))
                .collect(Collectors.toList());
    }

    protected <T> CallResultMapper<TarantoolResult<TarantoolTuple>,
            SingleValueCallResult<TarantoolResult<TarantoolTuple>>>
    getResultMapperForEntity(String spaceName, Class<T> entityClass) {
        TarantoolPersistentEntity<?> entityMetadata = mappingContext.getRequiredPersistentEntity(entityClass);
        String name = StringUtils.hasText(spaceName) ? spaceName : entityMetadata.getSpaceName();
        Optional<TarantoolSpaceMetadata> spaceMetadata = tarantoolClient.metadata()
                .getSpaceByName(name);
        if (!spaceMetadata.isPresent() && !entityClass.equals(void.class)) {
            throw new TarantoolMetadataMissingException(name);
        }

        return mapperFactoryFactory.createMapper(mapper)
                       .withSingleValueConverter(
                               mapperFactoryFactory.createMapper(mapper, spaceMetadata.orElse(null))
                                       .withArrayValueToTarantoolTupleResultConverter()
                                       .withRowsMetadataToTarantoolTupleResultConverter()
                                       .buildCallResultMapper(
                                               DefaultMessagePackMapperFactory.getInstance().emptyMapper()))
                       .buildCallResultMapper(DefaultMessagePackMapperFactory.getInstance().emptyMapper());
    }

    protected <T> T mapFirstToEntity(TarantoolResult<TarantoolTuple> tuples, Class<T> entityClass) {
        return mapToEntity(tuples.stream()
                        .findFirst()
                        .orElse(null),
                entityClass);
    }

    protected <T> T mapToEntity(@Nullable Object tuple, Class<T> entityClass) {
        return getConverter().read(entityClass, tuple);
    }

    protected Conditions createIndexEqualsConditionFromParts(List<?> indexPartValues) {
        List<?> indexPartValuesConverted = mapParameters(indexPartValues);
        return Conditions.indexEquals(TarantoolIndexQuery.PRIMARY, indexPartValuesConverted);
    }

    protected List<?> mapParameters(List<?> parameters) {
        List<Object> mappedParameters = new LinkedList<>();
        getConverter().write(parameters, mappedParameters);
        return mappedParameters;
    }

    protected MessagePackMapper getMessagePackMapper() {
        return tarantoolClient.getConfig().getMessagePackMapper();
    }
}
