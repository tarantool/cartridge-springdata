package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.BookStore;
import org.springframework.data.tarantool.entities.BookStoreId;
import org.springframework.data.tarantool.entities.BookTranslation;
import org.springframework.data.tarantool.entities.BookTranslationId;
import org.springframework.data.tarantool.repository.BookStoreRepository;
import org.springframework.data.tarantool.repository.BookTranslationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vladimir Rogach
 */
@Tag("integration")
class CompositePkIntegrationTest extends BaseIntegrationTest {

    @Autowired
    BookTranslationRepository bookTranslationRepository;

    @Autowired
    BookStoreRepository bookStoreRepository;

    @BeforeEach
    public void setUp() {
        bookTranslationRepository.deleteAll();
    }

    @Test
    public void findOne_shouldReturnNullForNonExistingKey() {
        BookTranslationId nonExistentId = BookTranslationId.builder()
                .bookId(1)
                .language("Alien")
                .edition(11)
                .build();
        Optional<BookTranslation> one = bookTranslationRepository.findById(nonExistentId);

        assertThat(one).isNotPresent();
    }

    @Test
    public void testSave() {
        BookTranslation translation = BookTranslation.builder()
                .bookId(2)
                .language("Russian")
                .edition(22)
                .translator("Ivan Ivanov")
                .comments("Some translation")
                .bytesString("Hello".getBytes())
                .build();
        BookTranslation newTranslation = bookTranslationRepository.save(translation);
        assertThat(newTranslation).isEqualTo(translation);
    }

    @Test
    public void testExists() {
        BookTranslationId id = BookTranslationId.builder()
                .bookId(3)
                .language("Russian")
                .edition(33)
                .build();

        BookTranslation translation = BookTranslation.builder()
                .bookId(id.getBookId())
                .language(id.getLanguage())
                .edition(id.getEdition())
                .translator("Petr Petrov")
                .comments("Another translation")
                .build();

        bookTranslationRepository.save(translation);

        assertTrue(bookTranslationRepository.existsById(id));
    }

    @Test
    public void testDelete() {
        BookTranslationId id = BookTranslationId.builder()
                .bookId(4)
                .language("Russian")
                .edition(44)
                .build();

        BookTranslation translation = BookTranslation.builder()
                .bookId(id.getBookId())
                .language(id.getLanguage())
                .edition(id.getEdition())
                .translator("Fedor Fedorov")
                .comments("Translation 11")
                .build();

        BookTranslation saved = bookTranslationRepository.save(translation);
        bookTranslationRepository.delete(saved);
        assertThat(bookTranslationRepository.existsById(id)).isFalse();
    }

    @Test
    public void testFindById() {
        BookTranslationId id = BookTranslationId.builder()
                .bookId(5)
                .language("Ukrainian")
                .edition(55)
                .build();

        BookTranslation translation = BookTranslation.builder()
                .bookId(id.getBookId())
                .language(id.getLanguage())
                .edition(id.getEdition())
                .translator("Mitro Dmitrienko")
                .comments("Translation 55")
                .build();

        bookTranslationRepository.save(translation);

        Optional<BookTranslation> translationResult = bookTranslationRepository.findById(id);
        assertThat(translationResult).hasValueSatisfying(actual -> {
            assertThat(actual.getTranslator()).isEqualTo(translation.getTranslator());
            assertThat(actual.getComments()).isEqualTo(translation.getComments());
        });
    }

    @Test
    public void testFindAll() {
        insertTranslations(Arrays.asList(6, 7, 8), "Lang678");
        List<BookTranslation> books = (List<BookTranslation>) bookTranslationRepository.findAll();
        assertEquals(3, books.size());
    }

    @Test
    public void testIdWithNulls() {
        //save entity with id = 9
        insertTranslations(Collections.singletonList(9), "Lang9");

        //save one more entity with id=9
        bookTranslationRepository.save(BookTranslation.builder()
                .bookId(9)
                .language("Lang99")
                .edition(99)
                .translator("DummyTranslator99")
                .comments("DummyComment99")
                .build());

        //language = null
        //edition = null
        BookTranslationId id = BookTranslationId.builder().bookId(9).build();

        // Nulls are not allowed in primary index - so Id type can not contain nulls.
        Assertions.assertThrows(DataRetrievalFailureException.class, () -> bookTranslationRepository.findById(id));

        id.setLanguage("Lang9");
        id.setEdition(90);
        Optional<BookTranslation> translationResult = bookTranslationRepository.findById(id);

        assertThat(translationResult).hasValueSatisfying(actual -> {
            assertThat(actual.getLanguage()).isEqualTo("Lang9");
            assertThat(actual.getTranslator()).isEqualTo("Google translate");
            assertThat(actual.getComments()).isEqualTo("Comment 9");
        });
    }

    @Test
    public void testEntityWithOptionalIdAndCustomType() {
        LocalDateTime ts = LocalDateTime.of(1985, 12, 30, 4, 30);
        bookStoreRepository.save(BookStore.builder()
                .bookId(1)
                .receivedAt(ts)
                .storeNumber(123)
                .build());

        //first entity must be returned
        Optional<BookStore> bookStoreRecord = bookStoreRepository.findById(BookStoreId.builder()
                .bookId(1)
                .receivedAt(ts).build());
        assertThat(bookStoreRecord).hasValueSatisfying(actual -> {
            assertThat(actual.getBookId()).isEqualTo(1);
            assertThat(actual.getStoreNumber()).isEqualTo(123);
            assertThat(actual.getReceivedAt()).isEqualTo(ts);
        });
    }

    private List<BookTranslationId> insertTranslations(List<Integer> bookIds, String language) {
        ArrayList<BookTranslationId> result = new ArrayList<BookTranslationId>();
        bookIds.forEach(id -> {
            BookTranslationId translationId = BookTranslationId.builder()
                    .bookId(id)
                    .language(language)
                    .edition(id * 10)
                    .build();

            BookTranslation translation = BookTranslation.builder()
                    .bookId(translationId.getBookId())
                    .language(translationId.getLanguage())
                    .edition(translationId.getEdition())
                    .translator("Google translate")
                    .comments("Comment " + id)
                    .build();

            bookTranslationRepository.save(translation);
            result.add(translationId);
        });
        return result;
    }
}
