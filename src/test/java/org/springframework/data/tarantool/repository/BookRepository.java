package org.springframework.data.tarantool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.tarantool.entities.Book;

import java.util.List;

/**
 * @author Alexey Kuzin
 */
public interface BookRepository extends CrudRepository<Book, Integer> {

    @Query(function = "find_by_complex_query", output = TarantoolSerializationType.TUPLE)
    List<Book> findByYearGreaterThenProxy(Integer year);

    @Query(function = "find_by_entity", output = TarantoolSerializationType.TUPLE)
    List<Book> findByBook(Book book);

    @Query(function = "update_by_complex_query", output = TarantoolSerializationType.TUPLE)
    Book updateYear(Integer id, Integer year);

    @Query(function = "update_by_complex_query", output = TarantoolSerializationType.TUPLE)
    void updateYearIncorrectReturnType(Integer id, Integer year);

    @Query(function = "update_by_complex_query_without_return")
    void updateYearCorrectReturnType(Integer id, Integer year);

    @Query(function = "book_find_list_by_name", output = TarantoolSerializationType.TUPLE)
    List<Book> getListByName(List<String> names);

    @Query(function = "batch_update_books", output = TarantoolSerializationType.TUPLE)
    List<Book> batchSave(List<Book> books);
}
