package org.springframework.data.tarantool.core.query.support;

import io.tarantool.driver.api.TarantoolIndexQuery;
import org.springframework.data.domain.Sort;

import java.util.stream.Collectors;

/**
 * Translates Spring-specific query objects into Tarantool driver specific
 *
 * @author Alexey Kuzin
 */
public class Query {

    private static final int LIMIT_ALL = -1;
    private static final int OFFSET_NONE = -1;

    private Sort sort;
    private int rows = LIMIT_ALL;
    private long offset = OFFSET_NONE;

    private IndexDefinition indexDefinition;

    /**
     * Basic constructor.
     */
    public Query() {
    }

    /**
     * Basic constructor with specified index.
     *
     * @param indexDefinition index metadata
     */
    public Query(IndexDefinition indexDefinition) {
        this.indexDefinition = indexDefinition;
    }

    /**
     * Creates new instance of {@link Query} with given {@link Sort}.
     *
     * @param sort sort metadata
     */
    public Query(Sort sort) {
        this.sort = sort;
    }

    /**
     * How many matching tuples to return in result
     * @param limit the limit, negative means all
     * @return this query instance
     */
    public Query limit(int limit) {
        this.rows = limit;
        return this;
    }

    /**
     * How many tuples to skip from the start of the list of matching tuples
     * @param offset the offset, negative means none
     * @return this query instance
     */
    public Query skip(long offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Add given {@link Sort} to the query. There can be several sort criteria
     * @param sort sort criteria
     * @return this query instance
     */
    public Query orderBy(Sort sort) {
        if (sort == null) {
            return this;
        }
        if (this.sort == null) {
            this.sort = sort;
        } else {
            this.sort = this.sort.and(sort);
        }
        return this;
    }

    /**
     * Get specified index definition
     * @return index definition
     */
    public IndexDefinition getIndexDefinition() {
        return indexDefinition;
    }

    /**
     * Set specified index definition
     * @param indexDefinition index definition
     */
    public void setIndexDefinition(IndexDefinition indexDefinition) {
        this.indexDefinition = indexDefinition;
    }

    /**
     * Get all specified sort criteria
     * @return {@link Sort} instance
     */
    public Sort getSort() {
        return sort;
    }

    /**
     * Set sort criteria
     * @param sort sort criteria
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * Get row limit
     * @return row limit
     */
    public int getRows() {
        return rows;
    }

    /**
     * Set row limit
     * @param rows number of rows
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Get offset
     * @return offset
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Set offset
     * @param offset number of rows
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    public TarantoolIndexQuery toIndexQuery() {
        TarantoolIndexQuery query = new TarantoolIndexQuery(indexDefinition.getId())
                .withKeyValues(indexDefinition.getIndexKeyDefinitions().stream()
                        .map(IndexKeyDefinition::getValue)
                        .collect(Collectors.toList()));
        return query;
    }
}
