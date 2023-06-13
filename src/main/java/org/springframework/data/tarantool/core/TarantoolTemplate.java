package org.springframework.data.tarantool.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.stream.Collectors;

import org.msgpack.value.Value;
import org.springframework.data.tarantool.core.convert.TarantoolConverter;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.util.Assert;

import io.tarantool.driver.api.SingleValueCallResult;
import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.metadata.TarantoolSpaceMetadata;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import io.tarantool.driver.mappers.CallResultMapper;
import io.tarantool.driver.mappers.MessagePackMapper;
import io.tarantool.driver.mappers.converters.ValueConverter;

/**
 * This class contains call operations for invoking stored functions in Tarantool instance
 *
 * @author Alexey Kuzin
 * @author Oleg Kuznetsov
 * @author Artyom Dubinin
 */
public class TarantoolTemplate extends BaseTarantoolTemplate {

    public TarantoolTemplate(
            TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> tarantoolClient,
            TarantoolMappingContext mappingContext,
            TarantoolConverter converter,
            ForkJoinWorkerThreadFactory queryExecutorsFactory) {
        super(tarantoolClient, mappingContext, converter, queryExecutorsFactory);
    }

    @Override
    public <T> T callForTuple(String functionName, List<?> parameters, String spaceName, Class<T> entityClass) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        List<T> result = callForTupleList(functionName, parameters, spaceName, entityClass);
        return result != null && !result.isEmpty() ? result.get(0) : null;
    }

    @Override
    public <T> T callForTuple(String functionName,
                              List<?> parameters,
                              ValueConverter<Value, T> entityConverter) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityConverter, "Entity converter must not be null!");

        List<T> result = callForTupleList(functionName, parameters, entityConverter);
        return result != null && !result.isEmpty() ? result.get(0) : null;
    }

    @Override
    public <T> List<T> callForTupleList(String functionName, List<?> parameters,
                                        String spaceName, Class<T> entityClass) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        return executeSync(getResultSupplier(functionName, parameters, spaceName, entityClass));
    }

    @Override
    public <T> List<T> callForTupleList(String functionName,
                                        List<?> parameters,
                                        ValueConverter<Value, T> entityConverter) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityConverter, "Entity converter must not be null!");

        return executeSync(getCustomResultSupplier(
                functionName, parameters, getMessagePackMapper(), entityConverter));
    }

    @Override
    public <T> T callForObject(String functionName, List<?> parameters, ValueConverter<Value, T> entityConverter) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityConverter, "Entity converter must not be null!");
        return executeSync(() -> tarantoolClient.callForSingleResult(
                functionName, mapParameters(parameters), getMessagePackMapper(), entityConverter)
        );
    }

    @Override
    public <T> T callForObject(String functionName, List<?> parameters, Class<T> entityClass) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");
        return executeSync(
                () -> tarantoolClient.callForSingleResult(functionName, mapParameters(parameters), entityClass)
                        .thenApply(value -> value == null ? null : mapToEntity(value, entityClass))
        );
    }

    private CallResultMapper<Object, SingleValueCallResult<Object>>
    getAutoResultMapper(Optional<TarantoolSpaceMetadata> spaceMetadata) {
        MessagePackMapper copiedMapper = mapper.copy();
        return mapperFactoryFactory.createMapper(mapper)
                .buildSingleValueResultMapper(
                        mapperFactoryFactory.createMapper(mapper, spaceMetadata.orElse(null))
                                .withArrayValueToTarantoolTupleResultConverter()
                                .withRowsMetadataToTarantoolTupleResultConverter()
                                .buildCallResultMapper(copiedMapper),
                        Object.class);
    }

    @Override
    public <T> T callForObject(String functionName, List<?> parameters, Class<T> entityClass, String spaceName) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");

        Optional<TarantoolSpaceMetadata> spaceMetadata = tarantoolClient.metadata().getSpaceByName(spaceName);

        CallResultMapper<Object, SingleValueCallResult<Object>> resultMapper = getAutoResultMapper(spaceMetadata);

        return executeSync(
                () -> tarantoolClient.callForSingleResult(functionName, mapParameters(parameters), resultMapper)
                        .thenApply(value -> {
                            if (value == null) {
                                return null;
                            }
                            if (value instanceof TarantoolResult) {
                                return mapToEntity(((TarantoolResult) value).get(0), entityClass);
                            }
                            return mapToEntity(value, entityClass);
                        })
        );
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, List<?> parameters,
                                         Class<T> entityClass, String spaceName) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");
        Optional<TarantoolSpaceMetadata> spaceMetadata = tarantoolClient.metadata().getSpaceByName(spaceName);

        CallResultMapper<Object, SingleValueCallResult<Object>> resultMapper = getAutoResultMapper(spaceMetadata);

        return executeSync(
                () -> tarantoolClient.callForSingleResult(functionName, mapParameters(parameters), resultMapper)
                        .thenApply(values -> {
                            if (values == null) {
                                return null;
                            }
                            return ((List<T>) values).stream()
                                    .map(t -> mapToEntity(t, entityClass))
                                    .collect(Collectors.toList());
                        })
        );
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, List<?> parameters,
                                         ValueConverter<Value, T> entityConverter) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityConverter, "Entity converter must not be null!");

        return executeSync(getCustomResultSupplier(
                functionName, parameters, getMessagePackMapper(), entityConverter));
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, List<?> parameters, Class<T> entityClass) {
        Assert.hasText(functionName, "Function name must not be null or empty!");
        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(entityClass, "Entity class must not be null!");
        return executeSync(() -> tarantoolClient.callForSingleResult(functionName,
                mapParameters(parameters), getMessagePackMapper(), getListValueConverter(entityClass))
        );
    }

    @Override
    public <T> T callForTuple(String functionName, Class<T> entityType) {
        return callForTuple(functionName, Collections.emptyList(), "", entityType);
    }

    @Override
    public <T> T callForTuple(String functionName, ValueConverter<Value, T> entityConverter) {
        return callForTuple(functionName, Collections.emptyList(), entityConverter);
    }

    @Override
    public <T> T callForTuple(String functionName, Object[] parameters, Class<T> entityType) {
        return callForTuple(functionName, Arrays.asList(parameters), "", entityType);
    }

    @Override
    public <T> T callForTuple(String functionName, List<?> parameters, Class<T> entityType) {
        return callForTuple(functionName, parameters, "", entityType);
    }

    @Override
    public <T> T callForTuple(String functionName,
                              Object[] parameters,
                              ValueConverter<Value, T> entityConverter) {
        return callForTuple(functionName, Arrays.asList(parameters), entityConverter);
    }

    @Override
    public <T> T callForTuple(String functionName, Object[] parameters, String spaceName, Class<T> entityType) {
        return callForTuple(functionName, Arrays.asList(parameters), spaceName, entityType);
    }

    @Override
    public <T> T callForTuple(String functionName, String spaceName, Class<T> entityType) {
        return callForTuple(functionName, Collections.emptyList(), spaceName, entityType);
    }

    @Override
    public <T> List<T> callForTupleList(String functionName,
                                        Object[] parameters,
                                        ValueConverter<Value, T> entityConverter) {
        return callForTupleList(functionName, Arrays.asList(parameters), entityConverter);
    }

    @Override
    public <T> List<T> callForTupleList(String functionName, List<?> parameters, Class<T> entityType) {
        return callForTupleList(functionName, Collections.emptyList(), "", entityType);
    }

    @Override
    public <T> List<T> callForTupleList(String functionName, String spaceName, Class<T> entityType) {
        return callForTupleList(functionName, Collections.emptyList(), spaceName, entityType);
    }

    @Override
    public <T> List<T> callForTupleList(String functionName, ValueConverter<Value, T> entityConverter) {
        return callForTupleList(functionName, Collections.emptyList(), entityConverter);
    }

    @Override
    public <T> List<T> callForTupleList(String functionName, Object[] parameters, Class<T> entityType) {
        return callForTupleList(functionName, Arrays.asList(parameters), "", entityType);
    }

    @Override
    public <T> List<T> callForTupleList(String functionName, Object[] parameters,
                                        String spaceName, Class<T> entityType) {
        return callForTupleList(functionName, Arrays.asList(parameters), spaceName, entityType);
    }

    @Override
    public <T> List<T> callForTupleList(String functionName, Class<T> entityType) {
        return callForTupleList(functionName, Collections.emptyList(), "", entityType);
    }

    @Override
    public <T> T callForObject(String functionName, Class<T> entityType) {
        return callForObject(functionName, Collections.emptyList(), entityType);
    }

    @Override
    public <T> T callForObject(String functionName, ValueConverter<Value, T> entityConverter) {
        return callForObject(functionName, Collections.emptyList(), entityConverter);
    }

    @Override
    public <T> T callForObject(String functionName, Object[] parameters, Class<T> entityType) {
        return callForObject(functionName, Arrays.asList(parameters), entityType);
    }

    @Override
    public <T> T callForObject(String functionName, Object[] parameters, Class<T> entityType, String spaceName) {
        return callForObject(functionName, Arrays.asList(parameters), entityType, spaceName);
    }

    @Override
    public <T> T callForObject(String functionName, Object[] parameters, ValueConverter<Value, T> entityConverter) {
        return callForObject(functionName, Arrays.asList(parameters), entityConverter);
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, Object[] parameters, Class<T> entityClass) {
        return callForObjectList(functionName, Arrays.asList(parameters), entityClass);
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, Object[] parameters,
                                         Class<T> entityClass, String spaceName) {
        return callForObjectList(functionName, Arrays.asList(parameters), entityClass, spaceName);
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, Object[] parameters,
                                         ValueConverter<Value, T> entityConverter) {
        return callForObjectList(functionName, Arrays.asList(parameters), entityConverter);
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, Class<T> entityType) {
        return callForObjectList(functionName, Collections.emptyList(), entityType);
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, ValueConverter<Value, T> entityConverter) {
        return callForObjectList(functionName, Collections.emptyList(), entityConverter);
    }
}
