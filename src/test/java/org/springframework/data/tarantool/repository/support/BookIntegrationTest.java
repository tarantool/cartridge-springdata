package org.springframework.data.tarantool.repository.support;

import io.tarantool.driver.api.conditions.Conditions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.Book;
import org.springframework.data.tarantool.entities.TestSpace;
import org.springframework.data.tarantool.repository.BookAsTestSpaceRepository;
import org.springframework.data.tarantool.repository.BookRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexey Kuzin
 * @author Artyom Dubinin
 * @author Ivan Dneprov
 */
@Tag("integration")
class BookIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookAsTestSpaceRepository bookAsTestSpaceRepository;

    @BeforeEach
    public void setUp() {
        Book donQuixote = Book.builder()
                .id(1).uniqueKey("a1").name("Don Quixote").author("Miguel de Cervantes").year(1605).build();
        Book theGreatGatsby = Book.builder()
                .id(2).uniqueKey("a2").name("The Great Gatsby").author("F. Scott Fitzgerald").year(1925).build();
        Book warAndPeace = Book.builder()
                .id(3).uniqueKey("a3").name("War and Peace").author("Leo Tolstoy").year(1869).build();
        bookRepository.save(donQuixote);
        bookRepository.save(theGreatGatsby);
        bookRepository.save(warAndPeace);
    }

    @AfterEach
    public void tearDown() {
        bookRepository.deleteAll();
    }

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
    public void test_deleteAll() {
        List<Book> books = (List<Book>) bookRepository.findAll();
        assertEquals(3, books.size());
        bookRepository.deleteAll();
        books = (List<Book>) bookRepository.findAll();
        assertEquals(0, books.size());
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
    public void testFindAll() {
        List<Book> books = (List<Book>) bookRepository.findAll();
        assertThat(books.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    public void testFindByYear() {
        List<Book> books = bookRepository.findByYearGreaterThenProxy(1000);
        assertThat(books.size()).isPositive();
    }

    @Test
    public void testFindByEntityWithTupleOutput() {
        Optional<Book> book = bookRepository.findById(3);
        assertThat(book.isPresent()).isTrue();
        List<Book> books = bookRepository.findByBookWithTupleOutput(book.get());
        assertThat(books.size()).isEqualTo(1);
        assertThat(books.get(0).getName()).isEqualTo("War and Peace");
    }

    @Test
    public void testFindByEntityWithTupleOutputAndNonListReturnType() {
        Optional<Book> expectedBook = bookRepository.findById(3);
        assertThat(expectedBook.isPresent()).isTrue();
        Book book = bookRepository.findByBookWithTupleOutputAndNonListReturnType(expectedBook.get());
        assertThat(book.getName()).isEqualTo("War and Peace");
    }

    @Test
    public void testFindByEntityWithAutoOutput() {
        Optional<Book> book = bookRepository.findById(3);
        assertThat(book.isPresent()).isTrue();
        List<Book> books = bookRepository.findByBookWithAutoOutput(book.get());
        assertThat(books.size()).isEqualTo(1);
        assertThat(books.get(0).getName()).isEqualTo("War and Peace");
    }

    @Test
    public void testFindByEntityWithAutoOutputAndNonListReturnType() {
        Optional<Book> expectedBook = bookRepository.findById(3);
        assertThat(expectedBook.isPresent()).isTrue();
        Book book = bookRepository.findByBookWithAutoOutputAndNonListReturnType(expectedBook.get());
        assertThat(book.getName()).isEqualTo("War and Peace");
    }

    @Test
    public void testComplexQueryWithMapReduce() {
        List<Book> books = bookRepository.getListByName(Arrays.asList("Don Quixote", "War and Peace"));
        assertThat(books.size()).isEqualTo(2);
        assertThat(books.get(0).getAuthor()).isEqualTo("Miguel de Cervantes");
        assertThat(books.get(0).getYear()).isEqualTo(1605);
    }

    @Test
    public void testUpdateYear() {
        Book book = Book.builder()
                .id(777).name("Red and Black")
                .uniqueKey("udf99").author("Stendal").year(1999).build();
        Book newBook = bookRepository.save(book);
        bookRepository.updateYear(777, 2000);
        Optional<Book> one = bookRepository.findById(777);
        assertTrue(one.isPresent());
        Book bookFromRepository = one.get();
        assertThat(bookFromRepository.getYear()).isEqualTo(2000);
    }

    @Test
    public void test_voidReturnType_shouldThrowException_withReturningNotNil() {
        Book book = Book.builder()
                .id(777).name("Red and Black")
                .uniqueKey("udf99").author("Stendal").year(1999).build();
        bookRepository.save(book);
        DataRetrievalFailureException exception =
                assertThrows(DataRetrievalFailureException.class,
                        () -> bookRepository.updateYearIncorrectReturnType(777, 2000));
        assertTrue(exception.getCause() instanceof MappingException);
        assertTrue(exception.getCause().getMessage().contains("Cannot map object of type class " +
                        "io.tarantool.driver.core.tuple.TarantoolTupleImpl to object of type void")
        );
    }

    @Test
    public void test_voidReturnType_shouldCompleteCorrectly_withReturningNil() {
        Book book = Book.builder()
                .id(777).name("Red and Black")
                .uniqueKey("udf99").author("Stendal").year(1999).build();
        Book newBook = bookRepository.save(book);
        bookRepository.updateYearCorrectReturnType(777, 2000);
        Optional<Book> one = bookRepository.findById(777);
        assertTrue(one.isPresent());
        Book bookFromRepository = one.get();
        assertThat(bookFromRepository.getYear()).isEqualTo(2000);
    }

    @Test
    public void testBatchSave() {
        Book book1 = Book.builder()
                .id(888).name("Tamerlan")
                .uniqueKey("udf888").author("Viktor Yan").year(1979).build();
        Book book2 = Book.builder()
                .id(888).name("Bratya Karamazovy")
                .uniqueKey("udf999").author("Fedor Dostoevsky").year(1888).build();
        List<Book> savedBooks = bookRepository.batchSave(Arrays.asList(book1, book2));
        assertTrue(savedBooks.size() > 0);
    }

    @Test
    void test_save_shouldSaveAndReturnBook_ifTestSpaceIsAClassName() {
        //given
        TestSpace entity = TestSpace.builder()
                .id(111)
                .name("Tales")
                .uniqueKey("udf65")
                .author("Grimm Brothers")
                .year(1569)
                .build();

        //when
        TestSpace saved = bookAsTestSpaceRepository.save(entity);

        //then
        assertThat(saved).isEqualTo(entity);
    }

    @Test
    void test_update_shouldUpdateOneRecord() {
        //given
        int id = 1;
        int year = 1569;

        TestSpace entity = TestSpace.builder()
            .id(id)
            .name("Tales")
            .uniqueKey("udf65")
            .author("Grimm Brothers")
            .year(1570)
            .build();

        bookAsTestSpaceRepository.save(entity);

        Conditions query = Conditions.equals("id", id);
        TestSpace entityForUpdate = TestSpace.builder()
            .year(year)
            .build();

        //when
        List<TestSpace> saved = bookAsTestSpaceRepository.update(query, entityForUpdate);

        //then
        assertThat(saved.get(0).getYear()).isEqualTo(year);
    }

    @Test
    void test_update_shouldUpdateMultipleRecords() {
        //given
        String author = "Mikhail Bulgakov";
        int year = 2023;

        TestSpace theMasterAndMargarita = TestSpace.builder()
            .id(0)
            .name("The Master and Margarita")
            .uniqueKey("uniqueKey0")
            .author(author)
            .year(1972)
            .build();
        bookAsTestSpaceRepository.save(theMasterAndMargarita);

        TestSpace dogsHeart = TestSpace.builder()
            .id(1)
            .name("Dog's Heart")
            .uniqueKey("uniqueKey1")
            .author(author)
            .year(1976)
            .build();
        bookAsTestSpaceRepository.save(dogsHeart);

        TestSpace flowersForAlgernon = TestSpace.builder()
            .id(2)
            .name("Flowers for Algernon")
            .uniqueKey("uniqueKey2")
            .author("Daniel Keyes")
            .year(1959)
            .build();
        bookAsTestSpaceRepository.save(flowersForAlgernon);

        Conditions query = Conditions.equals("author", author);
        TestSpace entityForUpdate = TestSpace.builder()
            .year(year)
            .build();

        //when
        List<TestSpace> saved = bookAsTestSpaceRepository.update(query, entityForUpdate);

        //then
        assertThat(saved.get(0).getYear()).isEqualTo(year);
        assertThat(saved.get(1).getYear()).isEqualTo(year);
        assertThat(saved.size()).isEqualTo(2);
    }
}
