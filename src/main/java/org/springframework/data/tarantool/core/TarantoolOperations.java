package org.springframework.data.tarantool.core;

import org.springframework.data.tarantool.core.convert.TarantoolConverter;
import org.springframework.data.tarantool.core.query.support.Query;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Interface that specifies a basic set of Tarantool operations. Implemented by {@link TarantoolTemplate}.
 *
 * @author Alexey Kuzin
 */
public interface TarantoolOperations {

    /**
     * Map the results of a query over a space for the entity class to a single instance of an object of the
     * specified type. Target space will be derived automatically from the entity class.
     * Default value mappers {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T> target entity type
     * @param query Query object that incapsulates the search criteria
     * @param entityType Desired type of the result object
     * @return The converted object
     */
    @Nullable
    <T> T findOne(Query query, Class<T> entityType);

    /**
     * Map the results of a query over a space for the entity class to a List of the specified type.
     * Target space will be derived automatically from the entity class.
     * Default value mappers {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T> target entity type
     * @param query Query object that incapsulates the search criteria
     * @param entityType Desired type of the result object
     * @return The list of converted objects
     */
    <T> List<T> find(Query query, Class<T> entityType);

    /**
     * Get an entity by the given id and map it to an object of the given type.
     * Target space will be derived automatically from the entity class.
     * Default converter {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T> target entity type
     * @param <ID> target entity index type
     * @param id Entity identifier
     * @param entityType Desired type of the result object
     * @return The converted object
     */
    <T, ID> T findById(ID id, Class<T> entityType);

    /**
     * Get all entities from a space and map them to a List of specified type. The space is determined automatically
     * from the entity class.
     * Default converter {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T> target entity type
     * @param entityType Desired type of the result object
     * @return The list of converted objects
     */
    <T> List<T> findAll(Class<T> entityType);

    /**
     * Map the results of a query over a space for the entity class to a List of the specified type. All entities
     * found are returned and removed from the space. Target space will be derived automatically from the entity class.
     * Default value mappers {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T> target entity type
     * @param query Query object that incapsulates the search criteria
     * @param entityType Desired type of the result object
     * @return The list of converted objects
     */
    @Nullable
    <T> List<T> findAndRemove(Query query, Class<T> entityType);

    /**
     * Count the number of records matching the specified query. The space is determined automatically
     * from the entity class.
     *
     * @param <T> target entity type
     * @param query Query object that incapsulates the search criteria
     * @param entityType Desired type of the result object
     * @return Number of records
     */
    <T> Long count(Query query, Class<T> entityType);

    /**
     * Insert a record into a space. The space is determined automatically by the entity class.
     *
     * @param <T> target entity type
     * @param entity The object to save
     * @param entityType Desired type of the result object
     * @return The inserted object
     */
    <T> T insert(T entity, Class<T> entityType);

    /**
     * Save a record into a space. The space is determined automatically by the entity class. If the record doesn't
     * exist, it will be inserted.
     *
     * @param <T> target entity type
     * @param entity The object to save
     * @param entityType Desired type of the result object
     * @return The inserted object
     */
    <T> T save(T entity, Class<T> entityType);

    /**
     * Remove a record from a space corresponding to the specified entity type.
     *
     * @param <T> target entity type
     * @param entity Target entity (must have the id property)
     * @param entityType Desired type of the result object
     * @return Removed entity value
     */
    @Nullable
    <T> T remove(T entity, Class<T> entityType);

    /**
     * Remove a record from a space corresponding to the specified entity type.
     *
     * @param <T> target entity type
     * @param <ID> target entity index type
     * @param id Target entity ID
     * @param entityType Desired type of the result object
     * @return Removed entity value
     */
    @Nullable
    <T, ID> T removeById(ID id, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns one entity as result.
     *
     * @param <T> target entity type
     * @param functionName callable API function name
     * @param parameters function parameters
     * @param entityType Desired type of the result object
     * @return function call result
     */
    @Nullable
    <T> T call(String functionName, Object[] parameters, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns a list of entities as result.
     *
     * @param <T> target entity type
     * @param functionName callable API function name
     * @param parameters function parameters
     * @param entityType Desired type of the result object
     * @return function call result
     */
    @Nullable
    <T> List<T> callForList(String functionName, Object[] parameters, Class<T> entityType);

    /**
     * Return the entity converter used for this instance
     * @return entity converter
     */
    TarantoolConverter getConverter();
}
