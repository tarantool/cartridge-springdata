package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.TestEntityWithFloatField;

public interface TestFloatRepository extends TarantoolRepository<TestEntityWithFloatField, Integer> {
}
