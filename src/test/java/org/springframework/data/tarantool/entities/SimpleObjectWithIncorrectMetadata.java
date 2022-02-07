package org.springframework.data.tarantool.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.tarantool.core.mapping.Tuple;

/**
 * @author Artyom Dubinin
 */
@Tuple("test_non_existent_space")
@Data
@Builder
@AllArgsConstructor
public class SimpleObjectWithIncorrectMetadata {
    Integer testId;
    Boolean testBoolean;
    String testString;
    Integer testInteger;
    Double testDouble;
}
