package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Tuple;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Alexey Kuzin
 */
@Data
@Builder
@EqualsAndHashCode
@Tuple("customers")
public class Customer {

    @Id
    private Long id;

    private String name;

    private List<String> tags;

    private Map<String, Address> addresses;

    private List<Address> foreignAddresses;

    private List<Book> favouriteBooks;

    private Address workAddress;

    private LocalDateTime lastVisitTime;
}
