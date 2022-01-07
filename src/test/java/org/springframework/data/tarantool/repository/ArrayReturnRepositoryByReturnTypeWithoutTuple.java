package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.SimpleArray;
import org.springframework.data.tarantool.entities.TestEntityWithDoubleField;

public interface ArrayReturnRepositoryByReturnTypeWithoutTuple extends TarantoolRepository<TestEntityWithDoubleField, Integer> {
    @Query(function = "returning_simple_array")
    SimpleArray getSimpleArray();
}
