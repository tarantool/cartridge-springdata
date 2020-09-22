package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.springframework.data.convert.TypeAliasAccessor;
import org.springframework.data.mapping.Alias;

/**
 * Actually reads and writes type alias into a tuple
 *
 * @author Alexey Kuzin
 */
public class TarantoolTupleTypeAliasAccessor implements TypeAliasAccessor<TarantoolTuple> {

    private String typeKey;

    public TarantoolTupleTypeAliasAccessor(final String typeKey) {
        this.typeKey = typeKey;
    }

    @Override
    public Alias readAliasFrom(TarantoolTuple source) {
        return Alias.ofNullable(source.getObject(typeKey, String.class).orElse(null));
    }

    @Override
    public void writeTypeTo(TarantoolTuple sink, Object alias) {
        if (typeKey != null) {
            sink.putObject(typeKey, alias);
        }
    }
}
