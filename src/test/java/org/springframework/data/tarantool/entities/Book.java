package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Field;
import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.repository.inheritance.AbstractEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Alexey Kuzin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@Tuple("test_space")
public class Book extends AbstractEntity {
    @Id
    private Integer id;

    @Field(name = "unique_key")
    private String uniqueKey;

    @Field(name = "book_name")
    private String name;

    private String author;

    private Integer year;

    private Address issuerAddress;

    private List<Address> storeAddresses;

    private List<Customer> readers;

    private LocalDate issueDate;
}
