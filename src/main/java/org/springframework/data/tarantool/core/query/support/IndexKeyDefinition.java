package org.springframework.data.tarantool.core.query.support;

/**
 * Class-container for index key parts definitions
 *
 * @author Alexey Kuzin
 */
public class IndexKeyDefinition {

    private Object value;

    public IndexKeyDefinition(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
