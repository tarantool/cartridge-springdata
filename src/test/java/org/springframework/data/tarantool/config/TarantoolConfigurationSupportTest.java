package org.springframework.data.tarantool.config;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.TestConfig;
import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.ObjectWithoutTuple;
import org.springframework.data.tarantool.repository.RepositoryWithoutTupleAnnotationOnEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Oleg Kuznetsov
 */
@Tag("integration")
class TarantoolConfigurationSupportTest extends BaseIntegrationTest {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private RepositoryWithoutTupleAnnotationOnEntity repositoryWithoutTupleAnnotationOnEntity;

    @Test
    @SneakyThrows
    void test_getInitialEntitySet_shouldReturnSetOfEntitiesOnlyWithTupleAnnotation() {
        //when
        Set<Class<?>> classes = testConfig.getInitialEntitySet();

        //then
        classes.forEach(clazz ->
                assertNotNull(AnnotatedElementUtils.findMergedAnnotation(clazz, Tuple.class))
        );
    }

    @Test
    @SneakyThrows
    void test_getInitialEntitySet_shouldReturnSetWhichNotContainEntityWithoutTupleAnnotation() {
        //when
        Set<Class<?>> classes = testConfig.getInitialEntitySet();
        //then
        assertFalse(classes.contains(ObjectWithoutTuple.class));
    }
}