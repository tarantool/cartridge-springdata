package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.Book;

import java.util.List;

/**
 * @author Alexey Kuzin
 */
public interface BookRepository extends TarantoolRepository<Book, Integer> {

    @Query(function = "find_by_complex_query", output = TarantoolSerializationType.TUPLE)
    List<Book> findByYearGreaterThenProxy(Integer year);

    @Query(function = "find_by_entity", output = TarantoolSerializationType.TUPLE)
    List<Book> findByBook(Book book);

    @Query(function = "update_by_complex_query", output = TarantoolSerializationType.TUPLE)
    void updateYear(Integer id, Integer year);

    @Query(function = "book_find_list_by_name", output = TarantoolSerializationType.TUPLE)
    List<Book> getListByName(List<String> names);

    @Query(function = "batch_update_books", output = TarantoolSerializationType.TUPLE)
    List<Book> batchSave(List<Book> books);
}
