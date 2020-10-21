package org.springframework.data.tarantool.repository;

import java.util.List;

/**
 * @author Alexey Kuzin
 */
public interface BookRepository extends TarantoolRepository<Book, Integer> {
//    List<Book> findByAuthor(String author);
//
//    List<Book> findByYearGreaterThan(Integer year);

    @Query(function = "find_by_complex_query")
    List<Book> findByYearGreaterThenProxy(Integer year);

    @Query(function = "update_by_complex_query")
    void updateYear(Integer id, Integer year);
}
