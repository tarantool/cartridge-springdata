package org.springframework.data.tarantool.repository.support;

import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.data.tarantool.core.mapping.TarantoolPersistentEntity;

/**
 * Tarantool-specific entity metadata
 *
 * @author Alexey Kuzin
 */
public class MappingTarantoolEntityInformation<T, ID> extends AbstractEntityInformation<T, ID>
        implements TarantoolEntityInformation<T, ID> {

    private final TarantoolPersistentEntity<T> entity;
    private final Class<?> idClass;

    public MappingTarantoolEntityInformation(TarantoolPersistentEntity<T> entity) {
        super(entity.getType());
        this.entity = entity;
        this.idClass = entity.hasIdProperty() ? entity.getIdProperty().getType() : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ID getId(T instance) {
        if (entity.hasIdProperty()) {
            return (ID) entity.getPropertyAccessor(instance).getProperty(entity.getIdProperty());
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ID> getIdType() {
        return (Class<ID>) idClass;
    }

    @Override
    public String getSpaceName() {
        return entity.getSpaceName();
    }

    @Override
    public String getIdAttribute() {
        return entity.hasIdProperty() ? entity.getIdProperty().getFieldName() : null;
    }
}
