package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.SimpleArray;

public interface ArrayReturnRepositoryByDomainTypeWithoutTuple extends TarantoolRepository<SimpleArray, Integer> {
    @Query(function = "returning_simple_array")
    SimpleArray getSimpleArray();
}
