package org.springframework.data.tarantool.repository;

import org.springframework.data.annotation.QueryAnnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to support the calling of API functions instead of client-side queries
 *
 * @author Alexey Kuzin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Documented
@QueryAnnotation
public @interface Query {
    /**
     * Specify the function name to invoke on the Tarantool instance, for example `my_query_function` or
     * `box.space.test:select`. If this annotation is specified, the method name will not be parsed into a query.
     *
     * @return the callable function name
     */
    String function();
}
