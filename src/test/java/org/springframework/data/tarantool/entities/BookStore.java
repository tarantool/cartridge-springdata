package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Field;
import org.springframework.data.tarantool.core.mapping.TarantoolIdClass;
import org.springframework.data.tarantool.core.mapping.Tuple;

import java.time.LocalDateTime;

/**
 * A test entity class with composite id.
 *
 * @author Vladimir Rogach
 */

@Data
@Builder
@EqualsAndHashCode
@Tuple("book_store")
@TarantoolIdClass(BookStoreId.class)
public class BookStore {

    // Test checks that static fields are ignored
    private static final String DUMMY_CONST_STR = "42";

    //@Id - this annotation is optional
    @Field(value = "id")
    private Integer bookId;

    private static final Integer DUMMY_CONST_INT = 42;

    //@Id - this annotation is optional
    @Field(value = "received_at")
    private LocalDateTime receivedAt;

    @Field(value="store_number")
    private Integer storeNumber;
}
