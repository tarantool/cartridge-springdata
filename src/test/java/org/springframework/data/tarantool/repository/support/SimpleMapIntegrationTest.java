package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.SimpleObject;
import org.springframework.data.tarantool.repository.SimpleObjectRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class SimpleMapIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private SimpleObjectRepository repository;

    @Test
    public void test_map_shouldCalledSuccessfully_withAnyValidation() {
        //given
        SimpleObject expected = SimpleObject.builder()
                .testId(null)
                .testBoolean(true)
                .testString("abc")
                .testInteger(123)
                .testDouble(1.23)
                .build();

        //when
        SimpleObject actual = repository.getSimpleMapWithAutoValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_map_shouldThrowException_withTupleValidation() {
        assertThrows(DataRetrievalFailureException.class, () -> repository.getSimpleMapWithTupleValidation());
    }

    @Test
    public void test_maps_shouldCalledSuccessfully_withAnyValidation() {
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
        List<SimpleObject> actual = repository.getSimpleMapsWithAutoValidation();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_maps_shouldThrowException_withTupleValidation() {
        assertThrows(
                DataRetrievalFailureException.class,
                () -> repository.getSimpleMapsWithTupleValidation()
        );
    }
}
