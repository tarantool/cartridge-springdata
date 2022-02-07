package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.TestEntityWithDoubleField;
import org.springframework.data.tarantool.entities.TestEntityWithFloatField;
import org.springframework.data.tarantool.entities.TestObject;
import org.springframework.data.tarantool.repository.CustomReturnTypeRepository;
import org.springframework.data.tarantool.repository.TestDoubleRepository;
import org.springframework.data.tarantool.repository.TestFloatRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oleg Kuznetsov
 * @author Artyom Dubinin
 */
@Tag("integration")
class CustomReturnTypesIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private CustomReturnTypeRepository repository;

    @Autowired
    private TestDoubleRepository testDoubleRepository;

    @Autowired
    private TestFloatRepository testFloatRepository;

    @AfterEach
    public void tearDown() {
        testDoubleRepository.deleteAll();
        testFloatRepository.deleteAll();
    }

    @Test
    public void test_nil_shouldCalledSuccessfully_withAutoOutput() {
        assertDoesNotThrow(() -> repository.getNilWithAutoOutput());
    }

    @Test
    public void test_nil_shouldCalledSuccessfully_withTupleOutput() {
        assertDoesNotThrow(() -> repository.getNilWithTupleOutput());
    }

    @Test
    public void should_testCustomConverter_returnObjectWithDouble_ifCustomConverterHasBeenAdded() {
        //given
        double testField = 1D;

        //when
        TestEntityWithDoubleField saved = testDoubleRepository.save(new TestEntityWithDoubleField(1, testField));

        //then
        assertEquals(testField, saved.getTest());
    }

    @Test
    public void should_test_returnObjectWithFloat_ifFirstInStackConvertersIsDouble() {
        //given
        float testField = 1f;

        //when
        TestEntityWithFloatField savedEntity = testFloatRepository.save(new TestEntityWithFloatField(1, testField));

        //then
        assertEquals(testField, savedEntity.getTest());
    }

    @Test
    public void should_test_returnIntegerFromRepository() {
        //given
        Integer expected = 1;

        //when
        Integer actual = testDoubleRepository.getInteger();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void should_test_returnStringFromRepository() {
        //given
        String expected = "test string";

        //when
        String actual = testDoubleRepository.getString();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void should_test_returnNonEntityObjectFromRepository() {
        //given
        TestObject expected = new TestObject("testString", 4);

        //when
        TestObject actual = testDoubleRepository.getNonEntityObject();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void should_test_returnNonEntityObjectListFromRepository() {
        //given
        final List<Object> expected = new ArrayList<>();
        TestObject expectedItem = new TestObject("testString", 4);
        TestObject expectedItem2 = new TestObject("testString2", 10);
        expected.add(expectedItem);
        expected.add(expectedItem2);


        //when
        List<TestObject> actual = testDoubleRepository.getNonEntityObjectList();

        //then
        assertEquals(expected, actual);
    }
}
