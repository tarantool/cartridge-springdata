package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.TestEntity;

import java.util.List;

public interface TestRepository extends TarantoolRepository<TestEntity, Integer> {

    @Tuple("test_custom_converter_space")
    @Query(function = "test_custom_converter")
    List<TestEntity> testCustomConverter(Integer id);
}
