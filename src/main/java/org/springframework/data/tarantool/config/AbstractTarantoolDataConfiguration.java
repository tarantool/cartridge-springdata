package org.springframework.data.tarantool.config;

import io.tarantool.driver.ClusterTarantoolClient;
import io.tarantool.driver.TarantoolClientConfig;
import io.tarantool.driver.TarantoolClusterAddressProvider;
import io.tarantool.driver.TarantoolServerAddress;
import io.tarantool.driver.auth.SimpleTarantoolCredentials;
import io.tarantool.driver.auth.TarantoolCredentials;
import io.tarantool.driver.core.TarantoolConnectionSelectionStrategies.ParallelRoundRobinStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.tarantool.core.DefaultTarantoolExceptionTranslator;
import org.springframework.data.tarantool.core.TarantoolExceptionTranslator;
import org.springframework.data.tarantool.core.TarantoolTemplate;
import io.tarantool.driver.api.TarantoolClient;
import org.springframework.data.tarantool.core.convert.MappingTarantoolConverter;
import org.springframework.data.tarantool.core.convert.TarantoolCustomConversions;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.repository.config.TarantoolRepositoryOperationsMapping;

import java.util.Collections;
import java.util.List;

/**
 * Base class for configuring Spring Data using JavaConfig with {@link TarantoolClient}.
 *
 * @author Alexey Kuzin
 */
@Configuration(proxyBeanMethods = false)
public abstract class AbstractTarantoolDataConfiguration extends TarantoolConfigurationSupport {

    /**
     * Create a cluster {@link TarantoolClient} instance. Constructs a {@link ClusterTarantoolClient}
     * instance by default.
     * Override {@link #tarantoolClientConfig(TarantoolCredentials)} to configure client settings and
     * {@link #tarantoolClusterAddressProvider()}} to configure the Tarantool server address.
     *
     * @return a client instance.
     * @see #tarantoolClientConfig(TarantoolCredentials)
     * @see #configureClientConfig(TarantoolClientConfig.Builder)
     * @see #tarantoolClusterAddressProvider()
     */
    @Bean(name = "clusterTarantoolClient", destroyMethod = "close")
    public TarantoolClient tarantoolClient(TarantoolClientConfig tarantoolClientConfig,
                                           TarantoolClusterAddressProvider tarantoolClusterAddressProvider) {
        return new ClusterTarantoolClient(
                tarantoolClientConfig, tarantoolClusterAddressProvider, ParallelRoundRobinStrategyFactory.INSTANCE);
    }

    /**
     * Creaate an intance of {@link TarantoolCredentials} for using in {@link TarantoolClientConfig}.
     *
     * @return a credentials instance
     */
    @Bean("tarantoolCredentials")
    public TarantoolCredentials tarantoolCredentials() {
        return new SimpleTarantoolCredentials();
    }

    /**
     * Return the {@link TarantoolClientConfig} used to create the actual {@literal TarantoolClient}. <br />
     * Override either this method, or use {@link #configureClientConfig(TarantoolClientConfig.Builder)}
     * to alter the setup.
     *
     * @return Default client configuration.
     */
    @Bean("tarantoolConfig")
    public TarantoolClientConfig tarantoolClientConfig(TarantoolCredentials tarantoolCredentials) {
        TarantoolClientConfig.Builder builder = new TarantoolClientConfig.Builder()
                .withCredentials(tarantoolCredentials);
        configureClientConfig(builder);
        return builder.build();
    }

    /**
     * Configure {@link TarantoolClientConfig} using the passed {@link TarantoolClientConfig.Builder}.
     *
     * @param builder never {@literal null}.
     */
    protected void configureClientConfig(TarantoolClientConfig.Builder builder) {
        // customization hook
    }

    /**
     * Configure the cluster nodes addresses provider by overriding this method. This provider may be
     * used in {@link #tarantoolClient(TarantoolClientConfig, TarantoolClusterAddressProvider)}
     * @return cluster address provider instance
     */
    @Bean("tarantoolClusterAddressProvider")
    public TarantoolClusterAddressProvider tarantoolClusterAddressProvider() {
        return () -> Collections.singletonList(tarantoolServerAddress());
    }

    /**
     * Override this method for providing a Tarantool server address for the default cluster client instance
     *
     * @return Tarantool server address
     */
    protected TarantoolServerAddress tarantoolServerAddress() {
        return new TarantoolServerAddress();
    }

    /**
     * Create a {@link TarantoolTemplate} instance.
     *
     * @param tarantoolClient a configured tarantool client instance
     * @param mappingContext mapping context, contains information about defined entities
     * @param converter type converter, converts data between entities and Tarantool tuples
     * @return a {@link TarantoolTemplate} instance.
     * @see #tarantoolClient(TarantoolClientConfig, TarantoolClusterAddressProvider)
     */
    @Bean("tarantoolTemplate")
    public TarantoolTemplate tarantoolTemplate(TarantoolClient tarantoolClient,
                                               TarantoolMappingContext mappingContext,
                                               MappingTarantoolConverter converter) {
        return new TarantoolTemplate(tarantoolClient, mappingContext, converter);
    }

    /**
     * Create a {@link TarantoolRepositoryOperationsMapping} instance.
     *
     * @param tarantoolTemplate a {@link TarantoolTemplate} instance
     */
    @Bean("tarantoolRepositoryOperationsMapping")
    public TarantoolRepositoryOperationsMapping tarantoolRepositoryOperationsMapping(
            TarantoolTemplate tarantoolTemplate) {
        // create a base mapping that associates all repositories to the default template
        TarantoolRepositoryOperationsMapping baseMapping = new TarantoolRepositoryOperationsMapping(tarantoolTemplate);
        // let the user tune it
        configureRepositoryOperationsMapping(baseMapping);
        return baseMapping;
    }

    /**
     * Override this method for configuring the TarantoolTemplate mapping to repositories
     *
     * @param baseMapping the default mapping (will associate all repositories to the default template)
     */
    protected void configureRepositoryOperationsMapping(TarantoolRepositoryOperationsMapping baseMapping) {
    }

    /**
     * Creates a {@link MappingTarantoolConverter} instance for the specified type conversions
     *
     * @param tarantoolMappingContext
     * @return an {@link MappingTarantoolConverter} instance
     * @see #customConversions()
     */
    @Bean("mappingTarantoolConverter")
    public MappingTarantoolConverter mappingTarantoolConverter(TarantoolMappingContext tarantoolMappingContext) {
        return new MappingTarantoolConverter(tarantoolMappingContext, customConversions());
    }

    /**
     * Creates a {@link TarantoolMappingContext} equipped with entity classes scanned from the mapping base package.
     *
     * @see #getMappingBasePackages()
     * @return TarantoolMappingContext instance
     * @throws ClassNotFoundException
     */
    @Bean("tarantoolMappingContext")
    public TarantoolMappingContext tarantoolMappingContext() throws ClassNotFoundException {

        TarantoolMappingContext mappingContext = new TarantoolMappingContext();
        mappingContext.setInitialEntitySet(getInitialEntitySet());
        mappingContext.setSimpleTypeHolder(customConversions().getSimpleTypeHolder());
        mappingContext.setFieldNamingStrategy(fieldNamingStrategy());

        return mappingContext;
    }

    @Bean("tarantoolCustomConversions")
    public TarantoolCustomConversions customConversions() {
        return new TarantoolCustomConversions(customConverters());
    }

    /**
     * Override this method for providing custom conversions
     * @return list of custom conversions
     */
    protected List<?> customConverters() {
        return Collections.emptyList();
    }

    /**
     * Returns the default exception translator
     *
     * @return exception translator
     */
    @Bean
    public TarantoolExceptionTranslator tarantoolExceptionTranslator() {
        return new DefaultTarantoolExceptionTranslator();
    }

}
