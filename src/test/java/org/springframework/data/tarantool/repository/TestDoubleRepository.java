package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.TestEntityWithDoubleField;
import org.springframework.data.tarantool.entities.TestObject;

import java.util.List;

@Tuple("test_custom_converter_space")
public interface TestDoubleRepository extends TarantoolRepository<TestEntityWithDoubleField, Integer> {

    @Query(function = "returning_number")
    Integer getInteger();

    @Query(function = "returning_string")
    String getString();

    @Query(function = "returning_object")
    TestObject getNonEntityObject();

    @Query(function = "returning_object_list")
    List<TestObject> getNonEntityObjectList();
}
