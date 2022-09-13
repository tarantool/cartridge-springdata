package org.springframework.data.tarantool.core;

import io.tarantool.driver.exceptions.TarantoolClientException;
import io.tarantool.driver.exceptions.TarantoolException;
import io.tarantool.driver.exceptions.TarantoolFunctionCallException;
import io.tarantool.driver.exceptions.TarantoolInternalException;
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
            if (cause instanceof TarantoolClientException || cause instanceof TarantoolFunctionCallException) {
                return new InvalidDataAccessResourceUsageException(cause.getMessage(), cause);
            }
            if (cause instanceof TarantoolInternalException) {
                return new DataRetrievalFailureException(cause.getMessage(), cause);
            }
            return new RecoverableDataAccessException(cause.getMessage(), cause);
        }
        // Do not convert unknown exceptions
        return null;
    }
}
