package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.core.mapping.Tuple;

/**
 * Constants that indicate which internal data structure the connector should expect and which converter stack to use.
 * This can be used both for validation and to speed up the conversion.
 * <ul>
 *   <li>
 *       <b>TUPLE</b> - can be used to accept flatten structures from Tarantool that don't have keys to map
 *       the incoming object to a java class object. For mapping, the keys(metadata) are obtained by calling
 *       the lua function <code>ddl.get_schema</code>.
 *       Accordingly, the returnType class must have a camelCase name corresponding to snake_cased space name
 *       in Tarantool, or this snake_cased name must be specified in the {@link Tuple} annotation.
 *       Also, the field structure should be the same as in the corresponding Tarantool space format.
 *       These flat structures can be passed to a connector with these serialization types:
 *       <ul>
 *         <li>
 *             <a href="https://www.tarantool.io/en/doc/latest/reference/reference_lua/box_space/">box.space</a>
 *             function response
 *         </li>
 *         <li><a href="https://github.com/tarantool/crud">crud</a> function response</li>
 *         <li><i>MP_ARRAY</i> - an array suitable for the metadata structure(in lua it's number indexed table)</li>
 *       </ul>
 *   </li>
 *   <li>
 *       <b>AUTO</b> - accepts all structures that TUPLE accepts.
 *       Additionally, it can also accept:
 *       <ul>
 *         <li><i>MP_MAP</i> - map with keys equal to the returnType object fields(in lua it's key indexed table)</li>
 *         <li>Scalar message pack types (in lua it's number, string, boolean, etc.)</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * @author Artyom Dubinin
 */
public enum TarantoolSerializationType {
    TUPLE,
    AUTO
}
