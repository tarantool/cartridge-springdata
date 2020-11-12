package org.springframework.data.tarantool.core.convert;

import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * A mapping converter for Tarantool.
 *
 * @author Alexey Kuzin
 */
public class MappingTarantoolConverter extends AbstractTarantoolConverter implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TarantoolMappingContext mappingContext;
    private final TarantoolMapTypeAliasAccessor mapTypeAliasAccessor;
    private final TarantoolTupleTypeMapper typeMapper;
    private final MappingTarantoolReadConverter readConverter;
    private final MappingTarantoolWriteConverter writeConverter;

    private ApplicationContext applicationContext;
    /*
     * Callbacks for Audit Mechanism
     */
    private @Nullable EntityCallbacks entityCallbacks;

    public MappingTarantoolConverter(TarantoolMappingContext mappingContext,
                                     TarantoolMapTypeAliasAccessor typeAliasAccessor,
                                     CustomConversions conversions) {
        super(conversions);
        this.mappingContext = mappingContext;
        this.mapTypeAliasAccessor = typeAliasAccessor;
        this.typeMapper = new TarantoolTupleTypeMapper();

        this.readConverter = new MappingTarantoolReadConverter(
                instantiators, mappingContext, typeMapper, mapTypeAliasAccessor, conversions, conversionService);
        this.writeConverter = new MappingTarantoolWriteConverter(
                typeMapper, mappingContext, conversions, conversionService);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        if (entityCallbacks == null) {
            setEntityCallbacks(EntityCallbacks.create(applicationContext));
        }
    }

    /**
     * Set the {@link EntityCallbacks} instance. Overrides potentially existing {@link EntityCallbacks}.
     *
     * @param entityCallbacks must not be {@literal null}.
     * @throws IllegalArgumentException if the given instance is {@literal null}.
     */
    public void setEntityCallbacks(EntityCallbacks entityCallbacks) {
        Assert.notNull(entityCallbacks, "EntityCallbacks must not be null");
        this.entityCallbacks = entityCallbacks;
    }

    @Override
    public <R> R read(final Class<R> clazz, TarantoolTuple source) {
        return readConverter.read(clazz, source);
    }

    @Override
    public void write(Object source, TarantoolTuple target) {
        writeConverter.write(source, target);
    }

    @Override
    public TarantoolMappingContext getMappingContext() {
        return mappingContext;
    }
}
