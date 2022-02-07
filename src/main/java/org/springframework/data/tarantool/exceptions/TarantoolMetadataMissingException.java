package org.springframework.data.tarantool.exceptions;

import io.tarantool.driver.exceptions.TarantoolException;

/**
 * This exception is thrown when we expect a flattened tuple,
 * but we cannot convert the values into object fields because the metadata is missing.
 *
 * @author Artyom Dubinin
 */
public class TarantoolMetadataMissingException extends TarantoolException {
    public TarantoolMetadataMissingException(String spaceName) {
        super(String.format("Missing metadata for space %s check name in Entity class", spaceName));
    }
}
