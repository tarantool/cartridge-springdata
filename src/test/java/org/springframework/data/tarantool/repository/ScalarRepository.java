package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.TestEntityWithDoubleField;

@Tuple("test_custom_converter_space")
public interface ScalarRepository extends TarantoolRepository<TestEntityWithDoubleField, Integer> {

    @Query(function = "returning_nil")
    void getNil();

    @Query(function = "returning_boolean")
    Boolean getBoolean();

    @Query(function = "returning_string")
    String getString();

    @Query(function = "returning_char")
    Character getChar();

    @Query(function = "returning_char")
    Byte getByte();

    @Query(function = "returning_integer")
    Integer getInteger();

    @Query(function = "returning_integer")
    Long getLong();

    @Query(function = "returning_integer")
    Short getShort();

    @Query(function = "returning_double")
    Double getDouble();

    @Query(function = "returning_double")
    Float getFloat();
}
