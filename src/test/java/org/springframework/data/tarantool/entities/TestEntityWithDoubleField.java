package org.springframework.data.tarantool.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Tuple;

@Data
@Tuple("test_custom_converter_space")
@AllArgsConstructor
public class TestEntityWithDoubleField {

    @Id
    private Integer id;
    private Double test;
}
