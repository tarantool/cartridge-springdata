package org.springframework.data.tarantool.core.mapping;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.annotation.Persistent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies domain object for saving into a Tarantool space
 *
 * @author Alexey Kuzin
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Tuple {

    /**
     * The name of Tarantool space where the marked class objects are supposed to be stored in. The space name will be
     * derived from the class name if not specified. Alias for {@link #spaceName()}.
     *
     * @return the name of space for storing the object
     */
    @AliasFor("spaceName")
    String value() default "";

    /**
     * The name of Tarantool space where the marked class objects are supposed to be stored in. The space name will be
     * derived from the class name if not specified. Alias for {@link #value()}.
     *
     * @return the name of space for storing the object
     */
    @AliasFor("value")
    String spaceName() default "";
}
