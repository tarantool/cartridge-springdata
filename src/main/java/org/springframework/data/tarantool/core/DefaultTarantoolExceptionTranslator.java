package org.springframework.data.tarantool.core;

import io.tarantool.driver.exceptions.TarantoolClientException;
import io.tarantool.driver.exceptions.TarantoolIndexNotFoundException;
import io.tarantool.driver.exceptions.TarantoolException;
import io.tarantool.driver.exceptions.TarantoolServerException;
import io.tarantool.driver.exceptions.TarantoolSpaceFieldNotFoundException;
import io.tarantool.driver.exceptions.TarantoolSpaceNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.RecoverableDataAccessException;

/**
 * Translates exceptions from Tarantool driver to the Spring Data's exceptions
 *
 * @author Alexey Kuzin
 */
public class DefaultTarantoolExceptionTranslator implements TarantoolExceptionTranslator {

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException cause) {
        if (cause instanceof TarantoolException) {
            // TODO Superclass in driver for metadata exceptions
            if (cause instanceof TarantoolClientException) {
                return new InvalidDataAccessResourceUsageException(cause.getMessage(), cause.getCause());
            }
            if (cause instanceof TarantoolServerException) {
                return new DataRetrievalFailureException(cause.getMessage(), cause.getCause());
            }
            return new RecoverableDataAccessException(cause.getMessage(), cause.getCause());
        }
        // Do not convert unknown exceptions
        return null;
    }
}
