package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Tuple;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Alexey Kuzin
 */
@Data
@Builder
@EqualsAndHashCode
@Tuple("customers")
public class Customer {
//    @Id
//    private UUID uuid;
    @Id
    private Long id;

    private String name;

    private List<String> tags;

    private Map<String, Address> addresses;
}
