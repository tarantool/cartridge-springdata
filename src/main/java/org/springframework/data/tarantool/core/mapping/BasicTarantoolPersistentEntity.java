package org.springframework.data.tarantool.core.mapping;

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.StringUtils;

/**
 * Basic representation of a persistent entity
 *
 * @author Alexey Kuzin
 */
public class BasicTarantoolPersistentEntity<T>
        extends BasicPersistentEntity<T, TarantoolPersistentProperty>
        implements TarantoolPersistentEntity<T> {

    public BasicTarantoolPersistentEntity(TypeInformation<T> information) {
        super(information);
    }

    @Override
    public String getSpaceName() {
        Tuple annotationField = getType().getAnnotation(Tuple.class);

        if (annotationField != null && StringUtils.hasText(annotationField.value())) {
            return annotationField.value();
        }

        return getType().getSimpleName();
    }

    @Override
    public boolean hasTupleAnnotation() {
        return getType().getAnnotation(Tuple.class) != null;
    }
}
