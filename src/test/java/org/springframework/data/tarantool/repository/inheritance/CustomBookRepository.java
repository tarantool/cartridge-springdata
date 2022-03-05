package org.springframework.data.tarantool.repository.inheritance;

import org.springframework.data.tarantool.entities.Book;

public interface CustomBookRepository extends CustomCrudRepository<Book, Integer> {
    @Override
    default String spaceName() {
        return "test_space";
    }
}
