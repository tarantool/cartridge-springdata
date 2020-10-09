package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.repository.Book;
import org.springframework.data.tarantool.repository.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Kuzin
 */
@Tag("integration")
class RepositoryIntegrationTest extends BaseIntegrationTest {
    @Autowired
    BookRepository bookRepository;

    @Test
    public void findOne_shouldReturnNullForNonExistingKey() {
        Optional<Book> one = bookRepository.findById(123);

        assertThat(one).isNotPresent();
    }

    @Test
    public void testSave() {
        Book book = Book.builder().id(4).name("Tales").uniqueKey("udf65").author("Grimm Brothers").year(1569).build();
        Book newBook = bookRepository.save(book);
        assertThat(newBook).isEqualTo(book);
    }

    @Test
    public void testExists() {
        Book book = Book.builder()
                .id(5).name("Lady of the Lake").uniqueKey("udf66").author("Anjey Sapkovski").year(1986).build();
        Book newBook = bookRepository.save(book);
        assertThat(bookRepository.existsById(5)).isTrue();
    }

    @Test
    public void testDelete() {
        Book book = Book.builder()
                .id(6).name("The Wizard of the Emerald Town")
                .uniqueKey("udf67").author("Leonid Volkov").year(1978).build();
        Book newBook = bookRepository.save(book);
        bookRepository.delete(newBook);
        assertThat(bookRepository.existsById(6)).isFalse();
    }

    @Test
    public void testFindById() {
        Optional<Book> book = bookRepository.findById(3);
        assertThat(book).hasValueSatisfying(actual -> {
            assertThat(actual.getName()).isEqualTo("War and Peace");
            assertThat(actual.getAuthor()).isEqualTo("Leo Tolstoy");
        });
    }

    @Test
    public void testFindByYear() {
        List<Book> books = bookRepository.findByYearGreaterThenProxy(1000);
        assertThat(books.size()).isGreaterThan(0);
    }

//    @Test
//    public void testFindByYear() {
//        List<Book> books = bookRepository.findByYearGreaterThan(1000);
//        assertThat(books.size()).isGreaterThan(0);
//    }
}