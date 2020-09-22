package org.springframework.data.tarantool.core.mapping;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.util.StringUtils;

/**
 * @author Alexey Kuzin
 */
public class BasicTarantoolPersistentProperty extends AnnotationBasedPersistentProperty<TarantoolPersistentProperty>
        implements TarantoolPersistentProperty {

    private final FieldNamingStrategy fieldNamingStrategy;

    /**
     * Basic constructor.
     *
     * @param property property descriptor
     * @param owner property owner
     * @param simpleTypeHolder type holder
     * @param fieldNamingStrategy entity field naming strategy
     */
    public BasicTarantoolPersistentProperty(Property property,
                                            TarantoolPersistentEntity<?> owner,
                                            SimpleTypeHolder simpleTypeHolder,
                                            FieldNamingStrategy fieldNamingStrategy) {
        super(property, owner, simpleTypeHolder);
        this.fieldNamingStrategy = fieldNamingStrategy == null ?
                PropertyNameFieldNamingStrategy.INSTANCE : fieldNamingStrategy;
    }

    @Override
    protected Association<TarantoolPersistentProperty> createAssociation() {
        return new Association<>(this, null);
    }

    @Override
    public String getFieldName() {
        Field annotationField = AnnotatedElementUtils.findMergedAnnotation(getField(), Field.class);

        if (annotationField != null && StringUtils.hasText(annotationField.value())) {
            return annotationField.value();
        }

        return fieldNamingStrategy.getFieldName(this);
    }
}
