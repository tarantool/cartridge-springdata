package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.springframework.data.convert.EntityWriter;

/**
 * Responsible for converting Java entities into the native TarantoolTuple or Map
 *
 * @param <T> entity type
 * @author Alexey Kuzin
 */
public interface TarantoolWriter<T> extends EntityWriter<T, Object> {
}
