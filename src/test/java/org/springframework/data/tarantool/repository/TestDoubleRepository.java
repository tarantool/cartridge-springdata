package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.TestEntityWithDoubleField;

public interface TestDoubleRepository extends TarantoolRepository<TestEntityWithDoubleField, Integer> {
}
