package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.tarantool.core.mapping.Tuple;


/**
 * @author Oleg Kuznetsov
 */
@Data
@Tuple("test_space")
@Builder
public class SampleUser {
    private String name;
    private String lastName;
}
