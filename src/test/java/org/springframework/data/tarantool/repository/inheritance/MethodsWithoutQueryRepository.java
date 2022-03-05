package org.springframework.data.tarantool.repository.inheritance;

import org.springframework.data.tarantool.entities.Book;
import org.springframework.data.tarantool.repository.TarantoolRepository;

import java.util.List;

public interface MethodsWithoutQueryRepository<T extends AbstractEntity, ID> extends TarantoolRepository<T, ID> {

    String getString();

    List<String> getStringList();

    Book getBook();

    List<Book> getBookList();
}
