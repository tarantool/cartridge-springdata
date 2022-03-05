package org.springframework.data.tarantool.repository.inheritance;

import org.springframework.data.tarantool.repository.Query;
import org.springframework.data.tarantool.repository.TarantoolRepository;
import org.springframework.data.tarantool.repository.TarantoolSerializationType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface CustomCrudRepository<T extends AbstractEntity, ID> extends TarantoolRepository<T, ID> {

    String spaceName();

    default Optional<List<T>> find(String operator, String indexOrField, Object value) {
        List<List<?>> conditions = Collections.singletonList(
                Arrays.asList(operator, indexOrField, value));
        return select(spaceName(), conditions);
    }

    @Query(function = "crud.select", output = TarantoolSerializationType.TUPLE)
    Optional<List<T>> select(String spaceName, List<List<?>> conditions);
}
