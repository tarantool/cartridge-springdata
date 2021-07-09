package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.Book;

import java.util.List;

/**
 * @author Alexey Kuzin
 */
@Tuple("test_space")
public interface BookRepository extends TarantoolRepository<Book, Integer> {

    @Query(function = "find_by_complex_query")
    List<Book> findByYearGreaterThenProxy(Integer year);

    @Query(function = "find_by_entity")
    List<Book> findByBook(Book book);

    @Query(function = "update_by_complex_query")
    void updateYear(Integer id, Integer year);

    @Query(function = "book_find_list_by_name")
    List<Book> getListByName(List<String> names);

    @Query(function = "batch_update_books")
    List<Book> batchSave(List<Book> books);
}
