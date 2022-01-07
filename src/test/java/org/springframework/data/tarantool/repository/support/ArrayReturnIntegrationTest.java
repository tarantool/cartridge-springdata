package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.SimpleArray;
import org.springframework.data.tarantool.repository.ArrayReturnRepositoryByDomainType;
import org.springframework.data.tarantool.repository.ArrayReturnRepositoryByDomainTypeWithoutTuple;
import org.springframework.data.tarantool.repository.ArrayReturnRepositoryByReturnType;
import org.springframework.data.tarantool.repository.ArrayReturnRepositoryByReturnTypeWithoutTuple;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class ArrayReturnIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private ArrayReturnRepositoryByReturnType repositoryByReturnTypeWithTuple;

    @Autowired
    private ArrayReturnRepositoryByDomainType repositoryByDomainTypeWithTuple;

    @Autowired
    private ArrayReturnRepositoryByReturnTypeWithoutTuple repositoryByReturnTypeWithoutTuple;

    @Autowired
    private ArrayReturnRepositoryByDomainTypeWithoutTuple repositoryByDomainTypeWithoutTuple;

    @Test
    @Disabled
    public void test_array_shouldCalledSuccessfully_byReturnType_withoutTuple() {
        //given
        SimpleArray expected = SimpleArray.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleArray actual = repositoryByReturnTypeWithoutTuple.getSimpleArray();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_array_shouldCalledSuccessfully_byReturnType_withTuple() {
        //given
        SimpleArray expected = SimpleArray.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleArray actual = repositoryByReturnTypeWithTuple.getSimpleArray();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_array_shouldCalledSuccessfully_byDomainType_withoutTuple() {
        //given
        SimpleArray expected = SimpleArray.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleArray actual = repositoryByDomainTypeWithoutTuple.getSimpleArray();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_array_shouldCalledSuccessfully_byDomainType_withTuple() {
        //given
        SimpleArray expected = SimpleArray.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleArray actual = repositoryByDomainTypeWithTuple.getSimpleArray();

        //then
        assertEquals(expected, actual);
    }


}
