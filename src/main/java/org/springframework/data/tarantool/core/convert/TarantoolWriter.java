package org.springframework.data.tarantool.core.convert;

import org.springframework.data.convert.EntityWriter;

/**
 * Responsible for converting Java entities into the native TarantoolTuple
 *
 * @param <T> entity type
 * @author Alexey Kuzin
 */
public interface TarantoolWriter<T> extends EntityWriter<T, Object> {
}
