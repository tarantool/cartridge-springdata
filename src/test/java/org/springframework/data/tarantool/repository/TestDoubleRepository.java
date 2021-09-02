package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.TestEntityWithDoubleField;

import java.util.List;

public interface TestDoubleRepository extends TarantoolRepository<TestEntityWithDoubleField, Integer> {

    @Tuple("test_custom_converter_space")
    @Query(function = "test_custom_converter")
    List<TestEntityWithDoubleField> testCustomConverter(Integer id);
}
