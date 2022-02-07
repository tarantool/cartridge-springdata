package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.TestSpace;

/**
 * @author Oleg Kuznetsov
 */
public interface BookAsTestSpaceRepository extends TarantoolRepository<TestSpace, Integer> {
}
