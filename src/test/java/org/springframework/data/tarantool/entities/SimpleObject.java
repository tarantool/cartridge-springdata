package org.springframework.data.tarantool.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.tarantool.core.mapping.Tuple;

/**
 * @author Artyom Dubinin
 */
@Tuple("test_simple_object")
@Data
@Builder
@AllArgsConstructor
public class SimpleObject {
    Integer testId;
    Boolean testBoolean;
    String testString;
    Integer testInteger;
    Double testDouble;
}
