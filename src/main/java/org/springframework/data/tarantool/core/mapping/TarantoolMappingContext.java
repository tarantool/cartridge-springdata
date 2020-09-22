package org.springframework.data.tarantool.core.mapping;

import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

/**
 * Default {@link org.springframework.data.mapping.context.MappingContext} implementation for Tarantool
 *
 * @author Alexey Kuzin
 */
public class TarantoolMappingContext
        extends AbstractMappingContext<BasicTarantoolPersistentEntity<?>, TarantoolPersistentProperty> {

    private static final FieldNamingStrategy DEFAULT_NAMING_STRATEGY = PropertyNameFieldNamingStrategy.INSTANCE;

    private FieldNamingStrategy fieldNamingStrategy;

    /**
     * Configures the {@link FieldNamingStrategy} to be used to determine the field name if no manual mapping
     * is applied. Defaults to a strategy using the plain property name.
     *
     * @param fieldNamingStrategy the {@link FieldNamingStrategy} to be used to determine the field name if no manual
     *                            mapping is applied.
     */
    public void setFieldNamingStrategy(final FieldNamingStrategy fieldNamingStrategy) {
        this.fieldNamingStrategy = fieldNamingStrategy == null ? DEFAULT_NAMING_STRATEGY : fieldNamingStrategy;
    }

    @Override
    protected <T> BasicTarantoolPersistentEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
        return new BasicTarantoolPersistentEntity<>(typeInformation);
    }

    @Override
    protected TarantoolPersistentProperty createPersistentProperty(Property property,
                                                                   BasicTarantoolPersistentEntity<?> owner,
                                                                   SimpleTypeHolder simpleTypeHolder) {
        return new BasicTarantoolPersistentProperty(property, owner, simpleTypeHolder, fieldNamingStrategy);
    }
}
