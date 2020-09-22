package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.springframework.data.convert.DefaultTypeMapper;

/**
 * Tarantool tuple value type mapper based on the special "class" field value
 *
 * @author Alexey Kuzin
 */
public class DefaultTarantoolTypeMapper extends DefaultTypeMapper<TarantoolTuple> {

    public static final String DEFAULT_TYPE_KEY = "_class";

    public DefaultTarantoolTypeMapper() {
        super(new TarantoolTupleTypeAliasAccessor(DEFAULT_TYPE_KEY));
    }

    public DefaultTarantoolTypeMapper(final String typeKey) {
        super(new TarantoolTupleTypeAliasAccessor(typeKey));
    }
}
