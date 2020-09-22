package org.springframework.data.tarantool.config;

/**
 * Contains default bean names for Tarantool beans. These are the names of the beans used by Spring Data Tarantool,
 * unless an explicit id is given to the bean either in the xml configuration or the
 * {@link AbstractTarantoolDataConfiguration java configuration}.
 *
 * @author Alexey Kuzin
 */
public class BeanNames {
    /**
     * The name for the bean that stores custom mapping between repositories and their backing tarantoolOperations.
     */
    public static final String TARANTOOL_OPERATIONS_MAPPING = "tarantoolRepositoryOperationsMapping";
}
