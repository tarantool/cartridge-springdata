package org.springframework.data.tarantool.core.convert;

import org.springframework.data.convert.DefaultTypeMapper;

import java.util.Map;

/**
 * Value type mapper for nested objects based on the special "class" field value
 *
 * @author Alexey Kuzin
 */
public class TarantoolMapTypeMapper extends DefaultTypeMapper<Map<String, Object>> {

    public TarantoolMapTypeMapper() {
        super(new TarantoolMapTypeAliasAccessor(TarantoolTupleTypeMapper.DEFAULT_TYPE_KEY));
    }

    public TarantoolMapTypeMapper(final String typeKey) {
        super(new TarantoolMapTypeAliasAccessor(typeKey));
    }
}
