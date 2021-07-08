package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.Book;

import java.util.Optional;

/**
 * @author Oleg Kuznetsov
 */
public interface BookRepositoryWithoutTuple extends TarantoolRepository<Book, Integer> {

    @Override
    @Tuple("test_space")
    Optional<Book> findById(Integer id);

    @Override
    @Tuple("test_space")
    Book save(Book book);

    @Query(function = "returning_book")
    Book returningBook();
}
