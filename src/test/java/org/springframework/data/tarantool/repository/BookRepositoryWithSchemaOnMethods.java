package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.BookNonEntity;

import java.util.Optional;

public interface BookRepositoryWithSchemaOnMethods extends TarantoolRepository<BookNonEntity, Integer> {

    @Tuple("test_space")
    @Query(function = "save_book")
    void saveBook(BookNonEntity bookNonEntity);

    @Tuple("test_space")
    @Query(function = "find_book_by_id")
    Optional<BookNonEntity> findBookById(Integer id);


}
