package org.springframework.data.tarantool.core.convert;

import org.springframework.data.convert.TypeAliasAccessor;
import org.springframework.data.mapping.Alias;

import java.util.Map;

/**
 * Actually reads and writes type alias into a nested object in tuple
 *
 * @author Alexey Kuzin
 */
public class TarantoolMapTypeAliasAccessor implements TypeAliasAccessor<Map<String, Object>> {

    private String typeKey;

    public TarantoolMapTypeAliasAccessor(final String typeKey) {
        this.typeKey = typeKey;
    }

    @Override
    public Alias readAliasFrom(Map<String, Object> source) {
        return Alias.ofNullable(source.get(typeKey));
    }

    @Override
    public void writeTypeTo(Map<String, Object> sink, Object alias) {
        if (typeKey != null) {
            sink.put(typeKey, alias);
        }
    }
}
