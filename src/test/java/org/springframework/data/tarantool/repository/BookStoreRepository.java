package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.BookStore;
import org.springframework.data.tarantool.entities.BookStoreId;
import org.springframework.data.tarantool.entities.BookTranslation;
import org.springframework.data.tarantool.entities.BookTranslationId;

/**
 * Test repo with composite PK.
 *
 * @author Vladimir Rogach
 */
public interface BookStoreRepository extends TarantoolRepository<BookStore, BookStoreId> {
}
