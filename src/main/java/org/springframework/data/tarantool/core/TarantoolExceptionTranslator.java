package org.springframework.data.tarantool.core;

import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * Represents a translator for the errors coming from the driver to the SpringData errors
 *
 * @author Alexey Kuzin
 */
public interface TarantoolExceptionTranslator extends PersistenceExceptionTranslator {
}
