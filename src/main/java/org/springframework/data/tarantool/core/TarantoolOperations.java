package org.springframework.data.tarantool.core;

import io.tarantool.driver.api.conditions.Conditions;
import org.springframework.data.tarantool.core.convert.TarantoolConverter;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Interface that specifies a set extensions of Tarantool operations. Implemented by {@link BaseTarantoolTemplate}.
 *
 * @author Alexey Kuzin
 * @author Oleg Kuznetsov
 */
public interface TarantoolOperations extends TarantoolCallOperations {

    /**
     * Map the results of a query over a space for the entity class to a List of the specified type.
     * Target space will be derived automatically from the entity class.
     * Default value mappers {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T>        target entity type
     * @param query      Query object that encapsulates the search criteria
     * @param entityType Desired type of the result object
     * @return The list of converted objects
     */
    <T> List<T> find(Conditions query, Class<T> entityType);

    /**
     * Map the results of a query over a space for the entity class to a single instance of an object of the
     * specified type. Target space will be derived automatically from the entity class.
     * Default value mappers {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T>        target entity type
     * @param query      Query object that encapsulates the search criteria
     * @param entityType Desired type of the result object
     * @return The converted object
     */
    @Nullable
    <T> T findOne(Conditions query, Class<T> entityType);

    /**
     * Get an entity by the given id and map it to an object of the given type.
     * Target space will be derived automatically from the entity class.
     * Default converter {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T>        target entity type
     * @param <ID>       target entity index type
     * @param id         Entity identifier
     * @param entityType Desired type of the result object
     * @return The converted object
     */
    @Nullable
    <T, ID> T findById(ID id, Class<T> entityType);

    /**
     * Get all entities from a space and map them to a List of specified type. The space is determined automatically
     * from the entity class.
     * Default converter {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T>        target entity type
     * @param entityType Desired type of the result object
     * @return The list of converted objects
     */
    <T> List<T> findAll(Class<T> entityType);

    /**
     * Map the results of a query over a space for the entity class to a List of the specified type. All entities
     * found are returned and removed from the space. Target space will be derived automatically from the entity class.
     * Default value mappers {@link } will be used unless a custom one is specified. -- TODO
     *
     * @param <T>        target entity type
     * @param query      Query object that encapsulates the search criteria
     * @param entityType Desired type of the result object
     * @return The list of converted objects
     */
    @Nullable
    <T> List<T> findAndRemove(Conditions query, Class<T> entityType);

    /**
     * Count the number of records matching the specified query. The space is determined automatically
     * from the entity class.
     *
     * @param <T>        target entity type
     * @param query      Query object that encapsulates the search criteria
     * @param entityType Desired type of the result object
     * @return Number of records
     */
    <T> Long count(Conditions query, Class<T> entityType);

    /**
     * Insert a record into a space. The space is determined automatically by the entity class.
     *
     * @param <T>        target entity type
     * @param entity     The object to save
     * @param entityType Desired type of the result object
     * @return The inserted object
     */
    @Nullable
    <T> T insert(T entity, Class<T> entityType);

    /**
     * Save a record into a space. The space is determined automatically by the entity class. If the record doesn't
     * exist, it will be inserted.
     *
     * @param <T>        target entity type
     * @param entity     The object to save
     * @param entityType Desired type of the result object
     * @return The inserted object
     */
    @Nullable
    <T> T save(T entity, Class<T> entityType);

    /**
     * Update all records selected by the specified conditions. The space is determined automatically by the
     * entity class. Warning: executing this operation on a large data set may cause OutOfMemory error or take
     * significant time to complete.
     *
     * @param query       tuple selection conditions
     * @param entity      entity with new data for update
     * @param entityClass target class of the result objects
     * @param <T>         target entity type
     * @return list of updated objects
     */
    <T> List<T> update(Conditions query, T entity, Class<T> entityClass);

    /**
     * Remove a record from a space corresponding to the specified entity type.
     *
     * @param <T>        target entity type
     * @param entity     Target entity (must have the id property)
     * @param entityType Desired type of the result object
     * @return Removed entity value
     */
    @Nullable
    <T> T remove(T entity, Class<T> entityType);

    /**
     * Remove a record from a space corresponding to the specified entity type.
     *
     * @param <T>        target entity type
     * @param <ID>       target entity index type
     * @param id         Target entity ID
     * @param entityType Desired type of the result object
     * @return Removed entity value
     */
    @Nullable
    <T, ID> T removeById(ID id, Class<T> entityType);

    /**
     * Getter for {@link TarantoolMappingContext}
     *
     * @return TarantoolMappingContext instance
     */
    TarantoolMappingContext getMappingContext();

    /**
     * Return the entity converter used for this instance
     *
     * @return entity converter
     */
    TarantoolConverter getConverter();

    /**
     * Truncate space (remove all data records in the space on each node where it persists).
     *
     * @param spaceName space name
     */
    void truncate(String spaceName);

}
