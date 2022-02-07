package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.TestEntityWithFloatField;

/**
 * @author Oleg Kuznetsov
 */
public interface TestFloatRepository extends TarantoolRepository<TestEntityWithFloatField, Integer> {
}
