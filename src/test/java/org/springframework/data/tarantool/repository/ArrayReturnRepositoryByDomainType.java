package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.SimpleArray;


@Tuple("test_simple_object")
public interface ArrayReturnRepositoryByDomainType extends TarantoolRepository<SimpleArray, Integer> {
    @Query(function = "returning_simple_array")
    SimpleArray getSimpleArray();
}
