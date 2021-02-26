package org.springframework.data.tarantool.core.convert;

import org.springframework.data.convert.EntityReader;

/**
 * Responsible for converting Tarantool tuples into the Java entities
 *
 * @param <T> entity type
 * @author Alexey Kuzin
 */
public interface TarantoolReader<T> extends EntityReader<T, Object> {
}
