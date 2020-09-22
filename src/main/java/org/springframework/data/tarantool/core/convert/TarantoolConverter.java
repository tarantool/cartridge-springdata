package org.springframework.data.tarantool.core.convert;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;

/**
 * Basic Tarantool entity-to-tuple converter interface
 *
 * @author Alexey Kuzin
 */
public interface TarantoolConverter extends TarantoolWriter<Object>, TarantoolReader<Object> {
    /**
     * Return the conversion service.
     *
     * @return the conversion service.
     */
    ConversionService getConversionService();

    /**
     * Return the mapping context used in this converter instance
     *
     * @return mapping context
     */
    TarantoolMappingContext getMappingContext();
}
