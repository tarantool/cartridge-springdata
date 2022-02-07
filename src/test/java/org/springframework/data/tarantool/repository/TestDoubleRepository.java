package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.TestEntityWithDoubleField;
import org.springframework.data.tarantool.entities.TestObject;

import java.util.List;

/**
 * @author Oleg Kuznetsov
 * @author Artyom Dubinin
 */
public interface TestDoubleRepository extends TarantoolRepository<TestEntityWithDoubleField, Integer> {

    @Query(function = "returning_number", output = TarantoolSerializationType.AUTO)
    Integer getInteger();

    @Query(function = "returning_string", output = TarantoolSerializationType.AUTO)
    String getString();

    @Query(function = "returning_object", output = TarantoolSerializationType.AUTO)
    TestObject getNonEntityObject();

    @Query(function = "returning_object_list", output = TarantoolSerializationType.AUTO)
    List<TestObject> getNonEntityObjectList();
}
