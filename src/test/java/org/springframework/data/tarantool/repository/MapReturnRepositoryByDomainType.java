package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.entities.SimpleArray;

import java.util.List;


@Tuple("test_simple_object")
public interface MapReturnRepositoryByDomainType extends TarantoolRepository<SimpleArray, Integer> {
    @Query(function = "returning_simple_map")
    SimpleArray getSimpleMap();

    @Query(function = "returning_array_of_identical_maps")
    List<SimpleArray> getArrayOfIdenticalMaps();
}
