package org.springframework.data.tarantool.core.mapping;

import org.springframework.data.annotation.Persistent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a composite primary key for this entity (marked as {@link Tuple}.
 *
 * The specified class must contain all properties
 * that are parts of primary index of the tuple.
 * These properties must be present in the entity
 * and may marked with {@link org.springframework.data.annotation.Id}.
 * &#064;Id annotation on properties is optional but It is recommended to use it
 * to make code more clear.
 * Types of properties in primary key class and the entity must correspond.
 *
 * Example:
 *
 * <pre>{@code
 *
 *      public class BookId {
 *         String name;
 *         String author;
 *      }
 *
 *      &#064;TarantoolIdClass(io.tarantool.example.BookId)
 *      &#064;Tuple(table="book")
 *      public class Book {
 *         &#064;Id String name;
 *         &#064;Id String author;
 *         ...
 *      }
 *
 * }</pre>
 *
 * @author Vladimir Rogach
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface TarantoolIdClass {

    Class<?> value();
}
