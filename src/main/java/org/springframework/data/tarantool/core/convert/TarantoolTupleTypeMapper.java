package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.springframework.data.convert.DefaultTypeMapper;

/**
 * Tarantool tuple value type mapper based on the special "class" field value
 *
 * @author Alexey Kuzin
 */
public class TarantoolTupleTypeMapper extends DefaultTypeMapper<TarantoolTuple> {

    public static final String DEFAULT_TYPE_KEY = "_class";

    public TarantoolTupleTypeMapper() {
        super(new TarantoolTupleTypeAliasAccessor(DEFAULT_TYPE_KEY));
    }

    public TarantoolTupleTypeMapper(final String typeKey) {
        super(new TarantoolTupleTypeAliasAccessor(typeKey));
    }
}
