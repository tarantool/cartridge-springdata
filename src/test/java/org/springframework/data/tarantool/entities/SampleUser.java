package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.tarantool.core.mapping.Tuple;


/**
 * @author Oleg Kuznetsov
 * @author Artyom Dubinin
 */
@Data
@Tuple
@Builder
public class SampleUser {
    private String name;
    private Integer age;
}
