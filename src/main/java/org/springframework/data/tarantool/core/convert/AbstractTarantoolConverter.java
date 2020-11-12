package org.springframework.data.tarantool.core.convert;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.EntityInstantiators;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * Base class for {@link TarantoolConverter} implementations. Sets up a {@link GenericConversionService} and
 * populates basic converters. Allows registering {@link CustomConversions}.
 *
 * @author Alexey Kuzin
 */
public abstract class AbstractTarantoolConverter implements TarantoolConverter, InitializingBean {

    protected final GenericConversionService conversionService;
    protected CustomConversions conversions;
    protected EntityInstantiators instantiators = new EntityInstantiators();

    /**
     * Basic constructor.
     *
     * @param conversions Custom type conversions, can be {@literal null}
     */
    public AbstractTarantoolConverter(@Nullable CustomConversions conversions) {
        this.conversionService = new GenericConversionService();
        DefaultConversionService.addCollectionConverters(conversionService);
        this.conversions = conversions == null ? new TarantoolCustomConversions(Collections.emptyList()) : conversions;
    }

    /**
     * Register the given custom conversions with the converter.
     *
     * @param conversions must not be {@literal null}.
     */
    public void setCustomConversions(CustomConversions conversions) {
        Assert.notNull(conversions, "Conversions must not be null!");
        this.conversions = conversions;
    }

    /**
     * Register {@link EntityInstantiators} for customizing entity instantiation.
     *
     * @param instantiators can be {@literal null}. If null, default {@link EntityInstantiators} will be used
     */
    public void setInstantiators(@Nullable EntityInstantiators instantiators) {
        this.instantiators = instantiators == null ? new EntityInstantiators() : instantiators;
    }

    @Override
    public CustomConversions getCustomConversions() {
        return conversions;
    }

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public void afterPropertiesSet() {
        conversions.registerConvertersIn(conversionService);
    }
}
