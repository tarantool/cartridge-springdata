package org.springframework.data.tarantool.repository.support;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.Book;
import org.springframework.data.tarantool.entities.BookNonEntity;
import org.springframework.data.tarantool.entities.SampleUser;
import org.springframework.data.tarantool.entities.TestSpace;
import org.springframework.data.tarantool.repository.BookAsTestSpaceRepository;
import org.springframework.data.tarantool.repository.BookRepositoryWithSchemaOnMethods;
import org.springframework.data.tarantool.repository.BookRepositoryWithSchemaOnRepository;
import org.springframework.data.tarantool.repository.BookRepositoryWithSchemaOnRepositoryAndMethods;
import org.springframework.data.tarantool.repository.BookRepositoryWithTuple;
import org.springframework.data.tarantool.repository.BookRepositoryWithoutTuple;
import org.springframework.data.tarantool.repository.SampleUserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author Oleg Kuznetsov
 */
@Tag("integration")
public class IntegrationWithoutTuplesTest extends BaseIntegrationTest {

    @Autowired
    private SampleUserRepository sampleUserRepository;

    @Autowired
    private BookRepositoryWithTuple bookRepositoryWithTuple;

    @Autowired
    private BookAsTestSpaceRepository bookAsTestSpaceRepository;

    @Autowired
    private BookRepositoryWithoutTuple bookRepositoryWithoutTuple;

    @Autowired
    private BookRepositoryWithSchemaOnMethods bookRepositoryWithSchemaOnMethods;

    @Autowired
    private BookRepositoryWithSchemaOnRepository bookRepositoryWithSchemaOnRepository;

    @Autowired
    private BookRepositoryWithSchemaOnRepositoryAndMethods bookRepositoryWithSchemaOnRepositoryAndMethods;

    @BeforeAll
    public static void setUp() throws Exception {
        tarantoolContainer.executeScript("test_setup.lua").get();
    }


    @BeforeEach
    @SneakyThrows
    void setUpTest() {
        tarantoolContainer.executeScript("test_teardown.lua").join();
    }

    @AfterAll
    @SneakyThrows
    public static void tearDown() {
        tarantoolContainer.executeScript("test_teardown.lua").join();
    }

    private final Book book = Book.builder()
            .id(4)
            .name("Tales")
            .uniqueKey("udf65")
            .author("Grimm Brothers")
            .year(1569)
            .build();

    private final BookNonEntity bookNonEntity = BookNonEntity.builder()
            .id(4)
            .name("Tales")
            .uniqueKey("udf65")
            .author("Grimm Brothers")
            .year(1569)
            .build();

    @Test
    public void test_returningSampleUserObject_shouldReturnPredefinedUser() {
        //given
        SampleUser expected = SampleUser.builder()
                .name("Vasya")
                .lastName("Vasiliev")
                .build();
        //when
        SampleUser sampleUser = sampleUserRepository.returningSampleUserObject("test");
        //then
        assertThat(sampleUser).isEqualTo(expected);
    }

    @Test
    public void test_getPredefinedUsers_shouldReturnPredefinedUsers() {
        //given
        SampleUser firstExpected = SampleUser.builder()
                .name("Vasya")
                .lastName("Vasiliev")
                .build();

        SampleUser secondExpected = SampleUser.builder()
                .name("Test")
                .lastName("Testov")
                .build();

        //when
        List<SampleUser> sampleUsers = sampleUserRepository.getPredefinedUsers();
        SampleUser firstActual = sampleUsers.get(0);
        SampleUser secondActual = sampleUsers.get(1);

        //then
        assertThat(firstActual).isEqualTo(firstExpected);
        assertThat(secondActual).isEqualTo(secondExpected);
    }


    @Test
    public void test_findById_shouldReturnBook_ifTupleAnnotationOnQueryMethodWithSpace() {
        bookRepositoryWithoutTuple.save(this.book);
        //when
        Optional<Book> book = bookRepositoryWithoutTuple.findById(this.book.getId());
        //then
        assertThat(book).hasValueSatisfying(actual -> {
            assertThat(actual.getName()).isEqualTo(this.book.getName());
            assertThat(actual.getAuthor()).isEqualTo(this.book.getAuthor());
        });
    }

    @Test
    public void test_save_shouldSaveBook_ifRepositoryMethodWithTupleAnnotation() {
        //when
        Book newBook = bookRepositoryWithoutTuple.save(this.book);
        //then
        assertThat(newBook).isEqualTo(this.book);
    }

    @Test
    public void test_findBookByAuthor_shouldThrowException_ifTupleAnnotationOnRepository() {
        // returning_book function return object, but findBookByAuthor expected tuple
        // for this case method should throw exception
        assertThrows(InvalidDataAccessResourceUsageException.class,
                () -> bookRepositoryWithTuple.findBookByAuthor("Grimm Brothers"));
    }

    @Test
    public void test_delete_shouldDeleteBook_ifTupleAnnotationOnRepository() {
        //given
        Book expected = bookRepositoryWithTuple.save(this.book);
        //when
        bookRepositoryWithTuple.delete(expected);
        //then
        assertThat(bookRepositoryWithTuple.existsById(6)).isFalse();
    }

    @Test
    public void test_save_shouldSaveBook_ifTupleAnnotationOnRepository() {
        //when
        Book newBook = bookRepositoryWithTuple.save(this.book);
        //then
        assertThat(newBook).isEqualTo(this.book);
    }

    @Test
    public void test_findById_shouldReturnBookAsTuple_ifTupleAnnotationHasOnRepository() {
        bookRepositoryWithTuple.save(this.book);
        //when
        Optional<Book> book = bookRepositoryWithTuple.findById(this.book.getId());
        //then
        assertThat(book).hasValueSatisfying(actual -> {
            assertThat(actual.getName()).isEqualTo(this.book.getName());
            assertThat(actual.getAuthor()).isEqualTo(this.book.getAuthor());
        });
    }

    @Test
    public void test_returningBook_shouldReturnBook_ifRepositoryAndQueryMethodWithoutTupleAnnotation() {
        //when
        Book actual = bookRepositoryWithoutTuple.returningBook();
        //then
        assertThat(actual).isEqualTo(this.book);
    }

    @Test
    public void test_save_shouldSaveBook_ifRepositoryMethodWithTupleAnnotationAndSpecifiedSchema() {
        //when
        bookRepositoryWithSchemaOnMethods.saveBook(this.bookNonEntity);
        Optional<BookNonEntity> book = bookRepositoryWithSchemaOnMethods.findBookById(this.bookNonEntity.getId());

        //then
        assertThat(book).hasValueSatisfying(actual -> assertThat(actual).isEqualTo(this.bookNonEntity));
    }

    @Test
    public void test_save_shouldSaveBook_ifRepositoryWithTupleAnnotationAndSpecifiedSchema() {
        //when
        BookNonEntity actual = bookRepositoryWithSchemaOnRepository.saveBook(this.bookNonEntity);

        //then
        assertThat(actual).isEqualTo(this.bookNonEntity);

        bookRepositoryWithoutTuple.deleteById(actual.getId());
    }

    @Test
    public void test_save_shouldSaveBook_ifRepositoryWithSpecifiedSchemaOnRepositoryAndMethods() {
        //given
        bookRepositoryWithSchemaOnRepositoryAndMethods.saveBook(this.bookNonEntity);

        //when
        Optional<BookNonEntity> book = bookRepositoryWithSchemaOnRepositoryAndMethods.findBookById(this.bookNonEntity.getId());

        //then
        assertThat(book).hasValueSatisfying(actual -> assertThat(actual).isEqualTo(this.bookNonEntity));
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
}
