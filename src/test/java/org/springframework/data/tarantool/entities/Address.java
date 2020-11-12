package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Alexey Kuzin
 */
@Data
@Builder
@EqualsAndHashCode
public class Address {
    private String city;

    private String street;

    private int number;
}
