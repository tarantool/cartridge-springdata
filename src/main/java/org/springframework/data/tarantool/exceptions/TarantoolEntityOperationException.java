package org.springframework.data.tarantool.exceptions;

import io.tarantool.driver.exceptions.TarantoolException;

/**
 * General entity exception class.
 *
 * @author Vladimir Rogach
 */
public class TarantoolEntityOperationException extends TarantoolException {
    private static final String entityErrorText = "Entity operation error for ";

    public TarantoolEntityOperationException(Class<?> entityCls, String details) {
        super(entityErrorText + entityCls.getTypeName() + ": " + details);
    }

    public TarantoolEntityOperationException(Class<?> entityCls, Throwable cause) {
        super(entityErrorText + entityCls.getTypeName(), cause);
    }

    public TarantoolEntityOperationException(Class<?> entityCls, String details, Throwable cause) {
        super(entityErrorText + entityCls.getTypeName() + ": " + details, cause);
    }

    public TarantoolEntityOperationException(String entityName, String details, Throwable cause) {
        super(entityErrorText + entityName + ": " + details, cause);
    }

}
