package org.springframework.data.tarantool.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.tarantool.core.mapping.Tuple;


@Data
@Builder
@AllArgsConstructor
@Tuple("test_simple_object")
public class SimpleMap {
    Integer testId;
    Boolean testBoolean;
    String testString;
    Integer testInteger;
    Double testDouble;
}
