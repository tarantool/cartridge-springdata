package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Field;
import org.springframework.data.tarantool.core.mapping.Tuple;

/**
 * @author Alexey Kuzin
 */
@Data
@Builder
@EqualsAndHashCode
@Tuple("test_space")
public class Book {
    @Id
    private Integer id;

    @Field(name = "unique_key")
    private String uniqueKey;

    @Field(name = "book_name")
    private String name;

    private String author;

    private Integer year;
}
