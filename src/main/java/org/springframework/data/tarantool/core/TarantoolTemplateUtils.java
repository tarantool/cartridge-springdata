package org.springframework.data.tarantool.core;

import io.tarantool.driver.api.conditions.Conditions;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentProperty;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * This class contains static utils functions for {@link TarantoolOperations} implementations
 *
 * @author Oleg Kuznetsov
 */
public class TarantoolTemplateUtils {

    /**
     * Return Conditions class for field id
     *
     * @param tuple  represents tuple for Tarantool Instance
     * @param entity represents an entity to be persisted
     * @return conditions class with information about query
     */
    static Conditions idQueryFromTuple(TarantoolTuple tuple, TarantoolPersistentEntity<?> entity) {
        List<?> idValue = tuple.getFields();
        if (entity != null) {
            TarantoolPersistentProperty idProperty = entity.getIdProperty();
            if (idProperty == null) {
                throw new MappingException("No ID property specified in persistent entity " + entity.getType());
            }
            Object fieldValue = tuple.getObject(idProperty.getFieldName(), idProperty.getType())
                    .orElseThrow(() -> new MappingException("ID property value is null"));
            idValue = Collections.singletonList(fieldValue);
        }
        return Conditions.indexEquals(0, idValue);
    }

    /**
     * Return index part values
     *
     * @param source type of an entity
     * @param entity represents an entity to be persisted
     * @param <T>    desired return type
     * @return list of index parts
     */
    static <T> List<?> getIndexPartValues(T source, TarantoolPersistentEntity<?> entity) {
        return entity.hasTarantoolIdClassAnnotation() ?
                getIndexPartsFromCompositeIdValue(source, entity)
                : Collections.singletonList(source);
    }

    /**
     * Return composite id parts
     *
     * @param idValue object as id in entity
     * @param entity  represents an entity to be persisted
     * @return list of index parts
     */
    static List<?> getIndexPartsFromCompositeIdValue(Object idValue, TarantoolPersistentEntity<?> entity) {
        //for each property get field name and map to idValue bean property value
        Optional<Class<?>> idClass = entity.getTarantoolIdClass();
        Assert.isTrue(idClass.isPresent(), "@TarantoolIdClass is not specified for entity " + entity);
        return entity.getCompositeIdParts(idValue);
    }

    private TarantoolTemplateUtils() {
    }

}
