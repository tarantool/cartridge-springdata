package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.TestEntityWithFloatField;

import java.util.List;

public interface TestFloatRepository extends TarantoolRepository<TestEntityWithFloatField, Integer> {

    @Tuple("test_get_object_space")
    @Query(function = "test_get_object_space_return_long")
    List<TestEntityWithFloatField> test(Integer id);
}
