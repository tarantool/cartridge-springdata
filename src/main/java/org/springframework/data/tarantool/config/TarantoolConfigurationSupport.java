package org.springframework.data.tarantool.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.model.CamelCaseAbbreviatingFieldNamingStrategy;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.core.mapping.Tuple;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for Tarantool Spring Data configuration to be extended for JavaConfig usage.
 *
 * @author Alexey Kuzin
 */
public abstract class TarantoolConfigurationSupport {

    /**
     * Returns the base packages to scan for entities mapped to Tarantool spaces at startup.
     * Will return the package name of the configuration class' (the concrete class, not this one here) by default.
     * So if you have a {@code com.acme.AppConfig} extending {@link TarantoolConfigurationSupport}
     * the base package will be considered {@code com.acme}
     * unless the method is overridden to implement alternate behavior.
     *
     * @return the base packages to scan for mapped {@link Tuple} classes or an empty collection to not enable scanning
     * for entities.
     */
    protected Collection<String> getMappingBasePackages() {

        Package mappingBasePackage = getClass().getPackage();
        return Collections.singleton(mappingBasePackage == null ? null : mappingBasePackage.getName());
    }

    /**
     * Scans the mapping base package for classes annotated with {@link Tuple}. By default, it scans for entities in
     * all packages returned by {@link #getMappingBasePackages()}.
     *
     * @return Set of classes marked with {@link Tuple} annotations
     * @throws ClassNotFoundException if the entity scan fails
     * @see #getMappingBasePackages()
     */
    protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {

        Set<Class<?>> initialEntitySet = new HashSet<Class<?>>();

        for (String basePackage : getMappingBasePackages()) {
            if (!StringUtils.hasText(basePackage)) {
                continue;
            }

            initialEntitySet.addAll(scanForEntities(basePackage));
        }

        return initialEntitySet;
    }

    /**
     * Scans the given base package for entities, i.e. Tarantool specific entity types annotated with {@link Tuple} and
     * {@link Persistent}.
     *
     * @param basePackage must not be {@literal null}.
     * @return Set of classes marked either with {@link Tuple} or {@link Persistent} annotations
     * @throws ClassNotFoundException if the entity scan fails
     */
    protected Set<Class<?>> scanForEntities(String basePackage) throws ClassNotFoundException {
        Set<Class<?>> initialEntitySet = new HashSet<Class<?>>();

        if (StringUtils.hasText(basePackage)) {

            ClassPathScanningCandidateComponentProvider componentProvider =
                    new ClassPathScanningCandidateComponentProvider(false);
            componentProvider.addIncludeFilter(new AnnotationTypeFilter(Tuple.class));
            componentProvider.addIncludeFilter(new AnnotationTypeFilter(Persistent.class));

            for (BeanDefinition candidate : componentProvider.findCandidateComponents(basePackage)) {
                initialEntitySet.add(
                        ClassUtils.forName(candidate.getBeanClassName(),
                                TarantoolConfigurationSupport.class.getClassLoader()));
            }
        }

        return initialEntitySet;
    }

    /**
     * Configures whether to abbreviate field names for domain objects by configuring a
     * {@link CamelCaseAbbreviatingFieldNamingStrategy} on the {@link TarantoolMappingContext} instance created.
     * For advanced customization needs, consider overriding {@link #fieldNamingStrategy()}.
     *
     * @return {@code true} if the fields must be abbreviated. Default is {@code false}.
     */
    protected boolean abbreviateFieldNames() {
        return false;
    }

    /**
     * Configures a {@link FieldNamingStrategy} on the {@link TarantoolMappingContext} instance created.
     *
     * @return {@link CamelCaseAbbreviatingFieldNamingStrategy} if {@link #abbreviateFieldNames()} returns {@code true}.
     */
    protected FieldNamingStrategy fieldNamingStrategy() {
        return abbreviateFieldNames() ? new CamelCaseAbbreviatingFieldNamingStrategy()
                : PropertyNameFieldNamingStrategy.INSTANCE;
    }

}
