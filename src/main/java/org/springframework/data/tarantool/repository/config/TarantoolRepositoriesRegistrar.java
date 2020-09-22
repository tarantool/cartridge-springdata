package org.springframework.data.tarantool.repository.config;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * Tarantool-specific {@link RepositoryBeanDefinitionRegistrarSupport} implementation.
 *
 * @author Alexey Kuzin
 */
public class TarantoolRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    /**
     * @see org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getAnnotation()
     */
    protected Class<? extends Annotation> getAnnotation() {
        return EnableTarantoolRepositories.class;
    }

    /**
     * @see org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getExtension()
     */
    protected RepositoryConfigurationExtension getExtension() {
        return new TarantoolRepositoryConfigurationExtension();
    }
}
