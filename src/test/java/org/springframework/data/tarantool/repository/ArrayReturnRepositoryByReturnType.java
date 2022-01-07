package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.SimpleArray;
import org.springframework.data.tarantool.entities.TestEntityWithDoubleField;


@Tuple("test_simple_object")
public interface ArrayReturnRepositoryByReturnType extends TarantoolRepository<TestEntityWithDoubleField, Integer> {
    @Query(function = "returning_simple_array")
    SimpleArray getSimpleArray();
}
