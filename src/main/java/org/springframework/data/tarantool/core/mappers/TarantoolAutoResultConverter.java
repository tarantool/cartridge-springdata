package org.springframework.data.tarantool.core.mappers;

import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.metadata.TarantoolSpaceMetadata;
import io.tarantool.driver.mappers.converters.ValueConverter;
import io.tarantool.driver.mappers.converters.value.custom.TarantoolResultConverter;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.StringValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

/**
 * This converter provides an ability to check if the received result is suitable for converting
 * to a {@link TarantoolResult}, i.e. is the response structure appropriate and is the metadata present
 *
 * @param <V> the source MessagePack entity type
 * @param <T> the target object type
 * @author Artyom Dubinin
 */
public class TarantoolAutoResultConverter<V extends Value, T> extends TarantoolResultConverter<V, T> {

    private static final StringValue RESULT_META = ValueFactory.newString("metadata");
    private static final StringValue RESULT_ROWS = ValueFactory.newString("rows");

    private final TarantoolSpaceMetadata spaceMetadata;

    public TarantoolAutoResultConverter(ValueConverter<ArrayValue, T> tupleConverter,
                                        TarantoolSpaceMetadata spaceMetadata) {
        super(tupleConverter);
        this.spaceMetadata = spaceMetadata;
    }

    @Override
    public boolean canConvertValue(Value value) {
        return isSpaceMetadataExist() && (isTuple(value) || isCrudResponse(value));
    }

    private boolean isSpaceMetadataExist() {
        return spaceMetadata != null;
    }

    private boolean isTuple(Value value) {
        // [[[],...]]
        return value.isArrayValue() && value.asArrayValue().size() > 0 && value.asArrayValue().get(0).isArrayValue();
    }

    private boolean isCrudResponse(Value value) {
        if (value.isMapValue()) {
            // [{"metadata" : [...], "rows": [...]}]
            Map<Value, Value> tupleMap = value.asMapValue().map();
            return hasRowsAndMetadata(tupleMap);
        }
        return false;
    }

    private static boolean hasRowsAndMetadata(Map<Value, Value> valueMap) {
        return valueMap.containsKey(RESULT_META) && valueMap.containsKey(RESULT_ROWS);
    }
}

