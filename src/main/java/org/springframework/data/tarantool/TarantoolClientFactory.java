package org.springframework.data.tarantool;

import org.springframework.dao.support.PersistenceExceptionTranslator;

public interface TarantoolClientFactory {
    /**
     * Return exception translator to be used for the client exceptions
     */
    PersistenceExceptionTranslator getExceptionTranslator();
}
