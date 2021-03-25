package org.springframework.data.tarantool.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A composite id class for test.
 *
 * @author Vladimir Rogach
 */
@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookStoreId {

    private static final String DUMMY_CONST_STR = "42";

    private Integer bookId;

    private static final Integer DUMMY_CONST_INT = 42;

    private LocalDateTime receivedAt;

}
