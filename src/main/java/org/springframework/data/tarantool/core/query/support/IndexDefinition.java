package org.springframework.data.tarantool.core.query.support;

import java.util.List;

/**
 * Class-container for index parameters (name, key parts values, etc)
 *
 * @author Alexey Kuzin
 */
public class IndexDefinition {

    public static final int PRIMARY = 0; // primary index

    private String name;
    private int id;
    private List<IndexKeyDefinition> indexKeyDefinitions;

    public IndexDefinition(String name, List<IndexKeyDefinition> indexKeyDefinitions) {
        this.name = name;
        this.indexKeyDefinitions = indexKeyDefinitions;
    }

    public IndexDefinition(int id, List<IndexKeyDefinition> indexKeyDefinitions) {
        this.id = id;
        this.indexKeyDefinitions = indexKeyDefinitions;
    }

    /**
     * Get index name
     * @return a String
     */
    public String getName() {
        return name;
    }

    /**
     * Get index ID
     * @return a number
     */
    public int getId() {
        return id;
    }

    public List<IndexKeyDefinition> getIndexKeyDefinitions() {
        return indexKeyDefinitions;
    }
}
