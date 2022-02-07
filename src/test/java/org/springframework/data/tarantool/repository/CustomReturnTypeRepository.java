package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.SimpleObject;

/**
 * @author Artyom Dubinin
 */
public interface CustomReturnTypeRepository extends TarantoolRepository<SimpleObject, Integer> {
    @Query(function = "returning_nothing", output = TarantoolSerializationType.AUTO)
    void getNilWithAutoOutput();

    @Query(function = "returning_nothing", output = TarantoolSerializationType.TUPLE)
    void getNilWithTupleOutput();
}
