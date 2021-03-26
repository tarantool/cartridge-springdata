package org.springframework.data.tarantool.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
public class BookTranslationId {
    private Integer bookId;
    private String language;
    private Integer edition;
}
