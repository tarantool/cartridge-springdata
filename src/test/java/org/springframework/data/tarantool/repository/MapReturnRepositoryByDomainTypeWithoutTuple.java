package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.SimpleArray;

import java.util.List;

public interface MapReturnRepositoryByDomainTypeWithoutTuple extends TarantoolRepository<SimpleArray, Integer> {
    @Query(function = "returning_simple_map")
    SimpleArray getSimpleMap();

    @Query(function = "returning_array_of_identical_maps")
    List<SimpleArray> getArrayOfIdenticalMaps();
}
