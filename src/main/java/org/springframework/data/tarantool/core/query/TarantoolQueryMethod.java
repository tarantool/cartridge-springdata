package org.springframework.data.tarantool.core.query;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.repository.Query;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Represents a query method with Tarantool extensions
 *
 * @author Alexey Kuzin
 */
public class TarantoolQueryMethod extends QueryMethod {

    private final Method method;
    private final RepositoryMetadata metadata;

    public TarantoolQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        super(method, metadata, factory);
        this.method = method;
        this.metadata = metadata;
    }

    public Optional<String> getSpaceName() {
        Tuple methodTupleAnnotation = getTupleAnnotation();
        if (methodTupleAnnotation != null) {
            return Optional.of(methodTupleAnnotation.spaceName());
        }

        Tuple declaredAnnotation = AnnotatedElementUtils.findMergedAnnotation(metadata.getRepositoryInterface(), Tuple.class);
        if (declaredAnnotation != null) {
            return Optional.of(declaredAnnotation.spaceName());
        }

        return Optional.empty();
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
     * If the method has a @Tuple annotation.
     *
     * @return true if it has the annotation, false otherwise.
     */
    public boolean hasTupleAnnotation() {
        return getTupleAnnotation() != null;
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
     * Returns the @Tuple annotation if set, null otherwise.
     *
     * @return the @Tuple annotation if present.
     */
    public Tuple getTupleAnnotation() {
        return AnnotatedElementUtils.findMergedAnnotation(method, Tuple.class);
    }

    /**
     * Return the callable function name specified in Query annotation
     *
     * @return API function name
     */
    public String getQueryFunctionName() {
        return getQueryAnnotation().function();
    }
}
