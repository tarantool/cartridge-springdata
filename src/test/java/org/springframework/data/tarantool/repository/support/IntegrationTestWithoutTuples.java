package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.Book;
import org.springframework.data.tarantool.entities.SampleUser;
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
public class IntegrationTestWithoutTuples extends BaseIntegrationTest {

    @Autowired
    private BookRepositoryWithTuple bookRepositoryWithTuple;

    @Autowired
    private BookRepositoryWithoutTuple bookRepositoryWithoutTuple;

    @Autowired
    private SampleUserRepository sampleUserRepository;

    @BeforeAll
    public static void setUp() throws Exception {
        tarantoolContainer.executeScript("test_setup.lua").get();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        tarantoolContainer.executeScript("test_teardown.lua").get();
    }

    private final Book book = Book.builder()
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
        //when
        Optional<Book> book = bookRepositoryWithoutTuple.findById(3);
        //then
        assertThat(book).hasValueSatisfying(actual -> {
            assertThat(actual.getName()).isEqualTo("War and Peace");
            assertThat(actual.getAuthor()).isEqualTo("Leo Tolstoy");
        });
    }

    @Test
    public void test_save_shouldSaveBook_ifRepositoryMethodWithTupleAnnotation() {
        //when
        Book newBook = bookRepositoryWithoutTuple.save(this.book);
        //then
        assertThat(newBook).isEqualTo(book);
    }

    @Test
    public void test_findBookByAuthor_shouldThrowException_ifTupleAnnotationOnRepository() {
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
        //when
        Optional<Book> book = bookRepositoryWithTuple.findById(3);
        //then
        assertThat(book).hasValueSatisfying(actual -> {
            assertThat(actual.getName()).isEqualTo("War and Peace");
            assertThat(actual.getAuthor()).isEqualTo("Leo Tolstoy");
        });
    }

    @Test
    public void test_returningBook_shouldReturnBook_ifRepositoryAndQueryMethodWithoutTupleAnnotation() {
        //when
        Book actual = bookRepositoryWithoutTuple.returningBook();
        //then
        assertThat(actual).isEqualTo(this.book);
    }
}
