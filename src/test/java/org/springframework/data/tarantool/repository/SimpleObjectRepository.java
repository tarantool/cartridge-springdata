package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.SimpleObject;
import org.springframework.data.tarantool.entities.SimpleObjectWithIncorrectMetadata;

import java.util.List;

/**
 * @author Artyom Dubinin
 */
public interface SimpleObjectRepository extends TarantoolRepository<SimpleObject, Integer> {
    @Query(function = "returning_simple_array")
    SimpleObject getSimpleTupleWithDefaultValidation();

    @Query(function = "returning_simple_array", output = TarantoolSerializationType.TUPLE)
    SimpleObject getSimpleTupleWithTupleValidation();

    @Query(function = "returning_simple_array", output = TarantoolSerializationType.AUTO)
    SimpleObject getSimpleTupleWithAutoValidation();

    @Query(function = "returning_simple_array", output = TarantoolSerializationType.TUPLE)
    SimpleObjectWithIncorrectMetadata getSimpleTupleWithTupleValidationAndIncorrectMetadata();

    @Query(function = "returning_simple_array", output = TarantoolSerializationType.AUTO)
    SimpleObjectWithIncorrectMetadata getSimpleTupleWithAutoValidationAndIncorrectMetadata();

    @Query(function = "returning_simple_arrays")
    List<SimpleObject> getSimpleTuplesWithDefaultValidation();

    @Query(function = "returning_simple_arrays", output = TarantoolSerializationType.TUPLE)
    List<SimpleObject> getSimpleTuplesWithTupleValidation();

    @Query(function = "returning_simple_arrays", output = TarantoolSerializationType.AUTO)
    List<SimpleObject> getSimpleTuplesWithAutoValidation();

    @Query(function = "returning_simple_arrays", output = TarantoolSerializationType.TUPLE)
    List<SimpleObjectWithIncorrectMetadata> getSimpleTuplesWithTupleValidationAndIncorrectMetadata();

    @Query(function = "returning_simple_arrays", output = TarantoolSerializationType.AUTO)
    List<SimpleObjectWithIncorrectMetadata> getSimpleTuplesWithAutoValidationAndIncorrectMetadata();

    @Query(function = "returning_crud_response_one_tuple", output = TarantoolSerializationType.TUPLE)
    SimpleObject getCrudResponseOneObjectWithTupleValidation();

    @Query(function = "returning_crud_response_one_tuple", output = TarantoolSerializationType.AUTO)
    SimpleObject getCrudResponseOneObjectWithAutoValidation();

    @Query(function = "returning_crud_response_two_tuples", output = TarantoolSerializationType.AUTO)
    List<SimpleObject> getCrudResponseTwoObjectsWithAutoValidation();

    @Query(function = "returning_crud_response_two_tuples", output = TarantoolSerializationType.TUPLE)
    List<SimpleObject> getCrudResponseTwoObjectsWithTupleValidation();

    @Query(function = "returning_simple_map", output = TarantoolSerializationType.TUPLE)
    SimpleObject getSimpleMapWithTupleValidation();

    @Query(function = "returning_simple_map", output = TarantoolSerializationType.AUTO)
    SimpleObject getSimpleMapWithAutoValidation();

    @Query(function = "returning_simple_maps", output = TarantoolSerializationType.AUTO)
    List<SimpleObject> getSimpleMapsWithAutoValidation();

    @Query(function = "returning_simple_maps", output = TarantoolSerializationType.TUPLE)
    List<SimpleObject> getSimpleMapsWithTupleValidation();
}
