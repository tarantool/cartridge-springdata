package org.springframework.data.tarantool.repository.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.data.tarantool.config.BeanNames;
import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.data.tarantool.repository.TarantoolRepository;
import org.springframework.data.tarantool.repository.support.TarantoolRepositoryFactoryBean;
import org.w3c.dom.Element;

/**
 * Tarantool specific implementation of {@link org.springframework.data.repository.config.RepositoryConfigurationExtension}
 * for different configuration options.
 *
 * @author Alexey Kuzin
 */
public class TarantoolRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

    /**
     * The reference property to use in xml configuration to specify the template to use with a repository.
     */
    private static final String TARANTOOL_TEMPLATE_REF = "tarantool-template-ref";

    @Override
    public String getModuleName() {
        return "Tarantool";
    }

    @Override
    protected String getModulePrefix() {
        return "tarantool";
    }


    @Override
    public String getRepositoryFactoryBeanClassName() {
        return TarantoolRepositoryFactoryBean.class.getName();
    }

    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.singleton(Tuple.class);
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.singleton(TarantoolRepository.class);
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
        Element element = config.getElement();
        ParsingUtils.setPropertyReference(builder, element, TARANTOOL_TEMPLATE_REF, "tarantoolOperations");
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
        builder.addDependsOn(BeanNames.TARANTOOL_OPERATIONS_MAPPING);
        builder.addPropertyReference("tarantoolOperationsMapping", BeanNames.TARANTOOL_OPERATIONS_MAPPING);
    }
}
