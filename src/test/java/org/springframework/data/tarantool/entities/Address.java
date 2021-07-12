package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.tarantool.core.mapping.Tuple;

/**
 * @author Alexey Kuzin
 */
@Data
@Builder
@Tuple("test_space")
@EqualsAndHashCode
public class Address {
    private String city;

    private String street;

    private int number;
}
