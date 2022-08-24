package org.springframework.data.tarantool.repository.support;

import io.tarantool.driver.exceptions.TarantoolAccessDeniedException;
import io.tarantool.driver.exceptions.TarantoolClientException;
import io.tarantool.driver.mappers.MessagePackValueMapperException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.RestrictedUserConfig;
import org.springframework.data.tarantool.entities.SimpleObject;
import org.springframework.data.tarantool.repository.SimpleObjectRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
@SpringBootTest(classes = RestrictedUserConfig.class)
class RestrictedUserIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private SimpleObjectRepository repository;

    @Test
    public void test_map_shouldThrowException_withTupleValidation_andAccessDeniedToMetadata() {
        TarantoolClientException exception
                = assertThrows(TarantoolClientException.class, () -> repository.getSimpleMapWithTupleValidation());
        assertTrue(exception.getCause() instanceof TarantoolAccessDeniedException);
    }

    @Test
    public void test_maps_shouldThrowException_withTupleValidation_andAccessDeniedToMetadata() {
        TarantoolClientException exception
                = assertThrows(TarantoolClientException.class, () -> repository.getSimpleMapsWithTupleValidation());
        assertTrue(exception.getCause() instanceof TarantoolAccessDeniedException);
    }

    @Test
    public void test_tuple_shouldThrowException_withAutoValidation_andAccessDeniedToMetadata() {
        DataRetrievalFailureException exception = assertThrows(
                DataRetrievalFailureException.class,
                () -> repository.getSimpleTupleWithAutoValidation()
        );
        assertTrue(exception.getCause() instanceof MappingException);
        assertTrue(exception.getCause().getMessage()
                .contains("Cannot map object of type class java.util.ArrayList to object"));
    }

    @Test
    public void test_tuples_shouldThrowException_withAutoValidation_andAccessDeniedToMetadata() {
        DataRetrievalFailureException exception = assertThrows(
                DataRetrievalFailureException.class,
                () -> repository.getSimpleTuplesWithAutoValidation()
        );
        // because we use mapper.fromValue(v, Map.class)
        assertTrue(exception.getCause() instanceof MessagePackValueMapperException);
    }

    @Test
    public void test_map_shouldCalledSuccessfully_withAutoValidation_andAccessDeniedToMetadata() {
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
    public void test_maps_shouldCalledSuccessfully_withAutoValidation_andAccessDeniedToMetadata() {
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
}
