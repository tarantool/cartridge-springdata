package org.springframework.data.tarantool.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Field;
import org.springframework.data.tarantool.core.mapping.TarantoolIdClass;
import org.springframework.data.tarantool.core.mapping.Tuple;

/**
 * A test entity class with composite id.
 *
 * @author Vladimir Rogach
 */

@Data
@Builder
@EqualsAndHashCode
@Tuple("book_translation")
@TarantoolIdClass(BookTranslationId.class)
public class BookTranslation {
    @Id
    @Field(value = "id")
    private Integer bookId;

    @Id
    private String language;

    @Id
    private Integer edition;

    private String translator;

    private String comments;
}
