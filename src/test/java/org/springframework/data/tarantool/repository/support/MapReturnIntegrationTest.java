package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.SimpleArray;
import org.springframework.data.tarantool.repository.MapReturnRepositoryByDomainType;
import org.springframework.data.tarantool.repository.MapReturnRepositoryByDomainTypeWithoutTuple;
import org.springframework.data.tarantool.repository.MapReturnRepositoryByReturnType;
import org.springframework.data.tarantool.repository.MapReturnRepositoryByReturnTypeWithoutTuple;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class MapReturnIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MapReturnRepositoryByReturnType repositoryByReturnTypeWithTuple;

    @Autowired
    private MapReturnRepositoryByDomainType repositoryByDomainTypeWithTuple;

    @Autowired
    private MapReturnRepositoryByReturnTypeWithoutTuple repositoryByReturnTypeWithoutTuple;

    @Autowired
    private MapReturnRepositoryByDomainTypeWithoutTuple repositoryByDomainTypeWithoutTuple;

    @Test
    public void test_map_shouldCalledSuccessfully_byReturnType_withoutTuple() {
        //given
        SimpleArray expected = SimpleArray.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleArray actual = repositoryByReturnTypeWithoutTuple.getSimpleMap();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_map_shouldCalledSuccessfully_byReturnType_withTuple() {
        //given
        SimpleArray expected = SimpleArray.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleArray actual = repositoryByReturnTypeWithTuple.getSimpleMap();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_map_shouldCalledSuccessfully_byDomainType_withoutTuple() {
        //given
        SimpleArray expected = SimpleArray.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleArray actual = repositoryByDomainTypeWithoutTuple.getSimpleMap();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_map_shouldCalledSuccessfully_byDomainType_withTuple() {
        //given
        SimpleArray expected = SimpleArray.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleArray actual = repositoryByDomainTypeWithTuple.getSimpleMap();

        //then
        assertEquals(expected, actual);
    }

    /////

    @Test
    public void test_listMaps_shouldCalledSuccessfully_byReturnType_withoutTuple() {
        //given
        List<SimpleArray> expected = Arrays.asList(
                SimpleArray.builder()
                        .testId(null)
                        .testBoolean(true)
                        .testString("abc")
                        .testInteger(123)
                        .testDouble(1.23)
                        .build(),
                SimpleArray.builder()
                        .testId(1)
                        .testBoolean(false)
                        .testString("cba")
                        .testInteger(321)
                        .testDouble(3.21)
                        .build()
        );

        //when
        List<SimpleArray> actual = repositoryByReturnTypeWithoutTuple.getArrayOfIdenticalMaps();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_listMaps_shouldCalledSuccessfully_byReturnType_withTuple() {
        //given
        List<SimpleArray> expected = Arrays.asList(
                SimpleArray.builder()
                        .testId(null)
                        .testBoolean(true)
                        .testString("abc")
                        .testInteger(123)
                        .testDouble(1.23)
                        .build(),
                SimpleArray.builder()
                        .testId(1)
                        .testBoolean(false)
                        .testString("cba")
                        .testInteger(321)
                        .testDouble(3.21)
                        .build()
        );

        //when
        List<SimpleArray> actual = repositoryByReturnTypeWithTuple.getArrayOfIdenticalMaps();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_listMaps_shouldCalledSuccessfully_byDomainType_withoutTuple() {
        //given
        List<SimpleArray> expected = Arrays.asList(
                SimpleArray.builder()
                        .testId(null)
                        .testBoolean(true)
                        .testString("abc")
                        .testInteger(123)
                        .testDouble(1.23)
                        .build(),
                SimpleArray.builder()
                        .testId(1)
                        .testBoolean(false)
                        .testString("cba")
                        .testInteger(321)
                        .testDouble(3.21)
                        .build()
        );

        //when
        List<SimpleArray> actual = repositoryByDomainTypeWithoutTuple.getArrayOfIdenticalMaps();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_listMaps_shouldCalledSuccessfully_byDomainType_withTuple() {
        //given
        List<SimpleArray> expected = Arrays.asList(
                SimpleArray.builder()
                        .testId(null)
                        .testBoolean(true)
                        .testString("abc")
                        .testInteger(123)
                        .testDouble(1.23)
                        .build(),
                SimpleArray.builder()
                        .testId(1)
                        .testBoolean(false)
                        .testString("cba")
                        .testInteger(321)
                        .testDouble(3.21)
                        .build()
        );

        //when
        List<SimpleArray> actual = repositoryByDomainTypeWithTuple.getArrayOfIdenticalMaps();

        //then
        assertEquals(expected, actual);
    }
}
