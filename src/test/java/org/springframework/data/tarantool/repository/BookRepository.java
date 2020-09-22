package org.springframework.data.tarantool.repository;

import java.util.List;

/**
 * @author Alexey Kuzin
 */
public interface BookRepository extends TarantoolRepository<Book, Integer> {
//    List<Book> findByAuthor(String author);
//
//    List<Book> findByYearGreaterThan(Integer year);
}
