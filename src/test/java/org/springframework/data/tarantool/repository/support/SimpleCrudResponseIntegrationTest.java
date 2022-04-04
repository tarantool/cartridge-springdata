package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.SimpleObject;
import org.springframework.data.tarantool.repository.SimpleObjectRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class SimpleCrudResponseIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private SimpleObjectRepository repository;

    @Test
    public void test_crudResponseOneObject_shouldCalledSuccessfully_withAutoOutput() {
        //given
        SimpleObject expected = SimpleObject.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleObject actual = repository.getCrudResponseOneObjectWithAutoValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_crudResponseOneObject_shouldCalledSuccessfully_withTupleOutput() {
        //given
        SimpleObject expected = SimpleObject.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleObject actual = repository.getCrudResponseOneObjectWithTupleValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_crudResponseTwoObjects_shouldCalledSuccessfully_withAutoOutput() {
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
        List<SimpleObject> actual = repository.getCrudResponseTwoObjectsWithAutoValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_crudResponseTwoObjects_shouldCalledSuccessfully_withTupleOutput() {
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
        List<SimpleObject> actual = repository.getCrudResponseTwoObjectsWithTupleValidation();

        //then
        assertEquals(expected, actual);
    }
}
