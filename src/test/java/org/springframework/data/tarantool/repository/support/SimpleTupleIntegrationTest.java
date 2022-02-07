package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.SimpleObject;
import org.springframework.data.tarantool.repository.SimpleObjectRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class SimpleTupleIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private SimpleObjectRepository repository;

    @Test
    public void test_tuple_shouldCalledSuccessfully_withDefaultValidation() {
        //given
        SimpleObject expected = SimpleObject.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleObject actual = repository.getSimpleTupleWithDefaultValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_tuple_shouldCalledSuccessfully_withTupleValidation() {
        //given
        SimpleObject expected = SimpleObject.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleObject actual = repository.getSimpleTupleWithTupleValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_tuple_shouldCalledSuccessfully_withAnyValidation() {
        //given
        SimpleObject expected = SimpleObject.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleObject actual = repository.getSimpleTupleWithAutoValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_tuple_shouldThrowException_withAutoValidationAndIncorrectMetadata() {
        try {
            repository.getSimpleTupleWithAutoValidationAndIncorrectMetadata();
            fail();
        } catch (DataRetrievalFailureException e) {
            assertEquals(MappingException.class, e.getCause().getClass());
        }
    }

    @Test
    public void test_tuples_shouldCalledSuccessfully_withDefaultValidation() {
        //given
        List<SimpleObject> expected = Arrays.asList(
                SimpleObject.builder()
                        .testId(null)
                        .testBoolean(true)
                        .testString("abc")
                        .testInteger(123)
                        .testDouble(1.23)
                        .build(),
                SimpleObject.builder()
                        .testId(1)
                        .testBoolean(false)
                        .testString("cba")
                        .testInteger(321)
                        .testDouble(3.21)
                        .build()
        );

        //when
        List<SimpleObject> actual = repository.getSimpleTuplesWithDefaultValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_tuples_shouldCalledSuccessfully_withTupleValidation() {
        //given
        List<SimpleObject> expected = Arrays.asList(
                SimpleObject.builder()
                        .testId(null)
                        .testBoolean(true)
                        .testString("abc")
                        .testInteger(123)
                        .testDouble(1.23)
                        .build(),
                SimpleObject.builder()
                        .testId(1)
                        .testBoolean(false)
                        .testString("cba")
                        .testInteger(321)
                        .testDouble(3.21)
                        .build()
        );

        //when
        List<SimpleObject> actual = repository.getSimpleTuplesWithTupleValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_tuples_shouldCalledSuccessfully_withAnyValidation() {
        //given
        List<SimpleObject> expected = Arrays.asList(
                SimpleObject.builder()
                        .testId(null)
                        .testBoolean(true)
                        .testString("abc")
                        .testInteger(123)
                        .testDouble(1.23)
                        .build(),
                SimpleObject.builder()
                        .testId(1)
                        .testBoolean(false)
                        .testString("cba")
                        .testInteger(321)
                        .testDouble(3.21)
                        .build()
        );

        //when
        List<SimpleObject> actual = repository.getSimpleTuplesWithAutoValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_tuples_shouldThrowException_withAutoValidationAndIncorrectMetadata() {
        try {
            repository.getSimpleTuplesWithAutoValidationAndIncorrectMetadata();
            fail();
        } catch (DataRetrievalFailureException e) {
            assertEquals(MappingException.class, e.getCause().getClass());
        }
    }
}
