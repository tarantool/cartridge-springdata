package org.springframework.data.tarantool.core;

import io.tarantool.driver.mappers.converters.ValueConverter;
import org.msgpack.value.Value;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Interface that specifies a set of Tarantool call operations extensions. Implemented by {@link TarantoolTemplate}.
 *
 * @author Oleg Kuznetsov
 * @author Artyom Dubinin
 */
public interface TarantoolCallOperations {

    /**
     * Call a function defined in Tarantool instance API which returns one entity as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param spaceName    space name in Tarantool instance
     * @param entityType   desired type of the result object
     * @return function call result
     */
    @Nullable
    <T> T callForTuple(String functionName, List<?> parameters, String spaceName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns some MessagePack value as result. The given
     * entity converter will be used for converting the result value into an entity.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param parameters      function parameters
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     */
    @Nullable
    <T> T callForTuple(String functionName, List<?> parameters, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns one object
     * in query method result format.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param parameters      function parameters
     * @param entityConverter converter from MessagePack value to the result object type
     * @return function call result
     * @see #callForObject(String, List, Class)
     */
    @Nullable
    <T> T callForObject(String functionName, List<?> parameters, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns one object
     * in query method result format.
     *
     * <pre>
     * {@code
     * function get_predefined_user()
     *     return { name = "John", lastName = "Smith" }
     * end
     * }
     * </pre>
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @return function call result
     */
    @Nullable
    <T> T callForObject(String functionName, List<?> parameters, Class<T> entityType);


    /**
     * Call a function defined in Tarantool instance API which returns one object
     * in query method result format.
     *
     * <pre>
     * {@code
     * function get_predefined_user()
     *     return { name = "John", lastName = "Smith" }
     * end
     * }
     * </pre>
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @param spaceName    name of the space to get the metadata, if needed
     * @return function call result
     */
    @Nullable
    <T> T callForObject(String functionName, List<?> parameters, Class<T> entityType, String spaceName);

    /**
     * Call a function defined in Tarantool instance API which returns list of objects
     * in query method result format.
     * The given entity converter will be used for converting each value in the result into an object.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param parameters      function parameters
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForObjectList(String, List, Class)
     */
    @Nullable
    <T> List<T> callForObjectList(String functionName, List<?> parameters, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns list of objects
     * in query method result format.
     *
     * <pre>
     * {@code
     * function get_predefined_users()
     *     return { { name: John, lastName: Smith }, { name: Sam, lastName: Brown } }
     * end
     * }
     * </pre>
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityClass  desired type of the result object
     * @param spaceName    name of the space to get the metadata, if needed
     * @return function call result
     */
    <T> List<T> callForObjectList(String functionName, List<?> parameters, Class<T> entityClass, String spaceName);

    /**
     * Call a function defined in Tarantool instance API which returns list of objects
     * in query method result format.
     *
     * <pre>
     * {@code
     * function get_predefined_users()
     *     return { { name: John, lastName: Smith }, { name: Sam, lastName: Brown } }
     * end
     * }
     * </pre>
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityClass  desired type of the result object
     * @return function call result
     */
    <T> List<T> callForObjectList(String functionName, List<?> parameters, Class<T> entityClass);


    /**
     * Call a function defined in Tarantool instance API which returns one entity as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> T callForTuple(String functionName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns some MessagePack value as result. The given
     * entity converter will be used for converting the result value into an entity.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> T callForTuple(String functionName, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns one entity as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> T callForTuple(String functionName, Object[] parameters, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns one entity as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> T callForTuple(String functionName, List<?> parameters, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns some MessagePack value as result. The given
     * entity converter will be used for converting the result value into an entity.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param parameters      function parameters
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> T callForTuple(String functionName, Object[] parameters, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns one entity as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param spaceName    space name in Tarantool instance
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> T callForTuple(String functionName, String spaceName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns one entity as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param spaceName    space name in Tarantool instance
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> T callForTuple(String functionName, Object[] parameters, String spaceName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns a list of entities as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns a list of MessagePack values as result. The given
     * entity converter will be used for converting each value in the result into an entity.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns a list of entities as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, Object[] parameters, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns a list of entities as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, List<?> parameters, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns a list of MessagePack values as result. The given
     * entity converter will be used for converting each value in the result into an entity.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param parameters      function parameters
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, Object[] parameters, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns a list of entities as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param spaceName    space name in Tarantool instance
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, String spaceName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns a list of entities as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param spaceName    space name in Tarantool instance
     * @param entityType   desired type of the result object
     * @return function call result
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, Object[] parameters, String spaceName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns a list of entities as result.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @param spaceName    space name in Tarantool instance
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, List<?> parameters, String spaceName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns a list of MessagePack values as result. The given
     * entity converter will be used for converting each value in the result into an entity.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param parameters      function parameters
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForTupleList(String, Object[], String, Class)
     */
    @Nullable
    <T> List<T> callForTupleList(String functionName, List<?> parameters, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns one object
     * in query method result format.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForObject(String, List, Class)
     */
    @Nullable
    <T> T callForObject(String functionName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns one object
     * in query method result format.
     * The given entity converter will be used for converting the result value into an entity.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForObject(String, List, Class)
     */
    @Nullable
    <T> T callForObject(String functionName, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns one object
     * in query method result format.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForObject(String, List, Class)
     */
    @Nullable
    <T> T callForObject(String functionName, Object[] parameters, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns one object
     * in query method result format.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @param spaceName    name of the space to get the metadata, if needed
     * @return function call result
     * @see #callForObject(String, List, Class)
     */
    @Nullable
    <T> T callForObject(String functionName, Object[] parameters, Class<T> entityType, String spaceName);

    /**
     * Call a function defined in Tarantool instance API which returns one object
     * in query method result format.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param parameters      function parameters
     * @param entityConverter converter from MessagePack value to the result object type
     * @return function call result
     * @see #callForObject(String, List, Class)
     */
    @Nullable
    <T> T callForObject(String functionName, Object[] parameters, ValueConverter<Value, T> entityConverter);

    /**
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForObjectList(String, List, Class)
     */
    @Nullable
    <T> List<T> callForObjectList(String functionName, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns list of objects
     * in query method result format.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForObjectList(String, List, Class)
     */
    @Nullable
    <T> List<T> callForObjectList(String functionName, ValueConverter<Value, T> entityConverter);

    /**
     * Call a function defined in Tarantool instance API which returns list of objects
     * in query method result format.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @param spaceName    name of the space to get the metadata, if needed
     * @return function call result
     * @see #callForObjectList(String, List, Class)
     */
    @Nullable
    <T> List<T> callForObjectList(String functionName, Object[] parameters, Class<T> entityType, String spaceName);

    /**
     * Call a function defined in Tarantool instance API which returns list of objects
     * in query method result format.
     *
     * @param <T>          target entity type
     * @param functionName callable API function name
     * @param parameters   function parameters
     * @param entityType   desired type of the result object
     * @return function call result
     * @see #callForObjectList(String, List, Class)
     */
    @Nullable
    <T> List<T> callForObjectList(String functionName, Object[] parameters, Class<T> entityType);

    /**
     * Call a function defined in Tarantool instance API which returns list of objects
     * in query method result format.
     * The given entity converter will be used for converting each value in the result into an object.
     *
     * @param <T>             target entity type
     * @param functionName    callable API function name
     * @param parameters      function parameters
     * @param entityConverter converter from MessagePack value to the result entity type
     * @return function call result
     * @see #callForObjectList(String, List, Class)
     */
    @Nullable
    <T> List<T> callForObjectList(String functionName, Object[] parameters, ValueConverter<Value, T> entityConverter);

}
