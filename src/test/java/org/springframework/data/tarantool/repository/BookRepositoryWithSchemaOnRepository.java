package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.BookNonEntity;

@Tuple("test_space")
public interface BookRepositoryWithSchemaOnRepository extends TarantoolRepository<BookNonEntity, Integer> {

    @Query(function = "save_book")
    BookNonEntity saveBook(BookNonEntity bookNonEntity);

}
