package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.Book;


/**
 * @author Oleg Kuznetsov
 */
@Tuple("test_space")
public interface BookRepositoryWithTuple extends TarantoolRepository<Book, Integer> {

    @Query(function = "returning_book")
    Book findBookByAuthor(String author);
}
