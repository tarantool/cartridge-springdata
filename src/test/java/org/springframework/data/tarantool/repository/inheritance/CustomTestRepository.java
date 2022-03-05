package org.springframework.data.tarantool.repository.inheritance;

import org.springframework.data.tarantool.entities.Book;
import org.springframework.data.tarantool.repository.Query;

public interface CustomTestRepository extends CustomCrudRepository<Book, Integer> {

    @Query(function = "returning_string")
    String getSampleString();
}
