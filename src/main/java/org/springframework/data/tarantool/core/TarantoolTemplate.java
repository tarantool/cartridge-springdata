package org.springframework.data.tarantool.core;

import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import io.tarantool.driver.mappers.ValueConverter;
import org.msgpack.value.Value;
import org.springframework.data.tarantool.core.convert.TarantoolConverter;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;

/**
 * @author Alexey Kuzin
 * @author Oleg Kuznetsov
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
    public <T> List<T> callForTupleList(String functionName, Object[] parameters, String spaceName, Class<T> entityType) {
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
    public <T> T callForObject(String functionName, Object[] parameters, ValueConverter<Value, T> entityConverter) {
        return callForObject(functionName, Arrays.asList(parameters), entityConverter);
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, Object[] parameters, Class<T> entityClass) {
        return callForObjectList(functionName, Arrays.asList(parameters), entityClass);
    }

    @Override
    public <T> List<T> callForObjectList(String functionName, Object[] parameters, ValueConverter<Value, T> entityConverter) {
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
