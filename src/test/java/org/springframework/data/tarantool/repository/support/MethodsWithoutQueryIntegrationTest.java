package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.repository.inheritance.MethodsWithoutQueryRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class MethodsWithoutQueryIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MethodsWithoutQueryRepository methodsWithoutQueriesRepository;

    @Test
    public void test_methodWithoutQuery_shouldThrowException_ifCallForObjectWithoutSpaceName() {
        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class, () -> methodsWithoutQueriesRepository.getString());
        assertTrue(exception.getMessage().contains("Function name must not be null or empty!"));
    }

    @Test
    public void test_methodWithoutQuery_shouldThrowException_ifCallForObjectWithSpaceName() {
        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class, () -> methodsWithoutQueriesRepository.getBook());
        assertTrue(exception.getMessage().contains("Function name must not be null or empty!"));
    }

    @Test
    public void test_methodWithoutQuery_shouldThrowException_ifCallForObjectListWithoutSpaceName() {
        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class, () -> methodsWithoutQueriesRepository.getStringList());
        assertTrue(exception.getMessage().contains("Function name must not be null or empty!"));
    }

    @Test
    public void test_methodWithoutQuery_shouldThrowException_ifCallForObjectListWithSpaceName() {
        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class, () -> methodsWithoutQueriesRepository.getBookList());
        assertTrue(exception.getMessage().contains("Function name must not be null or empty!"));
    }

}
