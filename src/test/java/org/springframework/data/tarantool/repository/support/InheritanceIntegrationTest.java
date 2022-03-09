package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.Book;
import org.springframework.data.tarantool.repository.inheritance.CustomBookRepository;
import org.springframework.data.tarantool.repository.inheritance.CustomTestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class InheritanceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private CustomBookRepository customBookRepository;
    @Autowired
    private CustomTestRepository customTestRepository;

    @BeforeEach
    public void setUp() {
        Book donQuixote = Book.builder()
                .id(1).uniqueKey("a1").name("Don Quixote").author("Miguel de Cervantes").year(1605).build();
        Book theGreatGatsby = Book.builder()
                .id(2).uniqueKey("a2").name("The Great Gatsby").author("F. Scott Fitzgerald").year(1925).build();
        Book warAndPeace = Book.builder()
                .id(3).uniqueKey("a3").name("War and Peace").author("Leo Tolstoy").year(1869).build();
        customBookRepository.save(donQuixote);
        customBookRepository.save(theGreatGatsby);
        customBookRepository.save(warAndPeace);
    }

    @AfterEach
    public void tearDown() {
        customBookRepository.deleteAll();
    }

    @Test
    public void test_customFind_should_if() {
        Optional<Book> firstBook = customBookRepository.findById(1);
        assertTrue(firstBook.isPresent());

        Optional<List<Book>> otherTwoBook = customBookRepository.find(">", "id", 1);
        assertTrue(otherTwoBook.isPresent());

    }

    @Test
    public void test_shouldReturnSampleString() {
        final String sampleString = customTestRepository.getSampleString();
        
        assertEquals("test string", sampleString);
    }
}
