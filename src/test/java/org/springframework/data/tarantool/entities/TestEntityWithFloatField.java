package org.springframework.data.tarantool.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Tuple;

@Data
@Tuple("test_get_object_space")
@AllArgsConstructor
public class TestEntityWithFloatField {

    @Id
    private Integer id;
    private Float test;
}
