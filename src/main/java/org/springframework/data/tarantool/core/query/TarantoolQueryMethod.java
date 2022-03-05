package org.springframework.data.tarantool.core.query;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.tarantool.repository.Query;

import java.lang.reflect.Method;

/**
 * Represents a query method with Tarantool extensions
 *
 * @author Alexey Kuzin
 * @author Artyom Dubinin
 */
public class TarantoolQueryMethod extends QueryMethod {

    private final Method method;
    private final RepositoryMetadata metadata;

    public TarantoolQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        super(method, metadata, factory);
        this.method = method;
        this.metadata = metadata;
    }

    /**
     * If the method has a @Query annotation.
     *
     * @return true if it has the annotation, false otherwise.
     */
    public boolean hasQueryAnnotation() {
        return getQueryAnnotation() != null;
    }

    /**
     * Returns the @Query annotation if set, null otherwise.
     *
     * @return the @Query annotation if present.
     */
    public Query getQueryAnnotation() {
        return AnnotatedElementUtils.findMergedAnnotation(method, Query.class);
    }

    /**
     * Return the callable function name specified in Query annotation
     *
     * @return API function name
     */
    public String getQueryFunctionName() {
        if (getQueryAnnotation() == null) {
            return null;
        }
        return getQueryAnnotation().function();
    }
}
