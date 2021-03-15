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
    private Integer bookId;
    private LocalDateTime receivedAt;
}
