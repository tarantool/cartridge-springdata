package org.springframework.data.tarantool.config;

import io.tarantool.driver.TarantoolClientConfig;
import io.tarantool.driver.TarantoolClusterAddressProvider;
import io.tarantool.driver.TarantoolServerAddress;
import io.tarantool.driver.api.ClusterTarantoolTupleClient;
import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import io.tarantool.driver.auth.SimpleTarantoolCredentials;
import io.tarantool.driver.auth.TarantoolCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.tarantool.core.DefaultTarantoolExceptionTranslator;
import org.springframework.data.tarantool.core.TarantoolExceptionTranslator;
import org.springframework.data.tarantool.core.TarantoolTemplate;
import org.springframework.data.tarantool.core.convert.MappingTarantoolConverter;
import org.springframework.data.tarantool.core.convert.TarantoolCustomConversions;
import org.springframework.data.tarantool.core.convert.TarantoolMapTypeAliasAccessor;
import org.springframework.data.tarantool.core.convert.TarantoolTupleTypeMapper;
import org.springframework.data.tarantool.core.mapping.TarantoolMappingContext;
import org.springframework.data.tarantool.core.mapping.TarantoolSimpleTypes;
import org.springframework.data.tarantool.repository.config.TarantoolRepositoryOperationsMapping;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;

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
     * @param tarantoolClientConfig           Tarantool client configuration
     * @param tarantoolClusterAddressProvider Tarantool cluster address provider
     * @return a client instance.
     * @see #tarantoolClientConfig(TarantoolCredentials)
     * @see #configureClientConfig(TarantoolClientConfig.Builder)
     * @see #tarantoolClusterAddressProvider()
     */
    @Bean(name = "clusterTarantoolClient", destroyMethod = "close")
    public TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>>
    tarantoolClient(TarantoolClientConfig tarantoolClientConfig,
                    TarantoolClusterAddressProvider tarantoolClusterAddressProvider) {
        return new ClusterTarantoolTupleClient(
                tarantoolClientConfig, tarantoolClusterAddressProvider);
    }

    /**
     * Create an instance of {@link TarantoolCredentials} for using in {@link TarantoolClientConfig}.
     *
     * @return a credentials instance
     */
    @Bean("tarantoolCredentials")
    public TarantoolCredentials tarantoolCredentials() {
        return new SimpleTarantoolCredentials();
    }

    /**
     * Return the {@link TarantoolClientConfig} used to create the actual {@literal TarantoolClient}.
     * <p>
     * Override either this method, or use {@link #configureClientConfig(TarantoolClientConfig.Builder)}
     * for altering the setup.
     *
     * @param tarantoolCredentials credentials for a user defined on target Tarantool instance
     * @return default client configuration
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
     *
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
     * @param tarantoolClient       a configured tarantool client instance
     * @param mappingContext        mapping context, contains information about defined entities
     * @param converter             type converter, converts data between entities and Tarantool tuples
     * @param queryExecutorsFactory worker thread factory for query executor threads
     * @return a {@link TarantoolTemplate} instance.
     * @see #tarantoolClient(TarantoolClientConfig, TarantoolClusterAddressProvider)
     */
    @Bean("tarantoolTemplate")
    public TarantoolTemplate tarantoolTemplate(TarantoolClient tarantoolClient,
                                               TarantoolMappingContext mappingContext,
                                               MappingTarantoolConverter converter,
                                               ForkJoinWorkerThreadFactory queryExecutorsFactory) {
        return new TarantoolTemplate(tarantoolClient, mappingContext, converter, queryExecutorsFactory);
    }

    /**
     * Create a {@link TarantoolRepositoryOperationsMapping} instance.
     *
     * @param tarantoolTemplate a {@link TarantoolTemplate} instance
     * @return a {@link TarantoolRepositoryOperationsMapping} instance
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
     * @param tarantoolMappingContext    a {@link TarantoolMappingContext} instance
     * @param typeAliasAccessor          a {@link TarantoolMapTypeAliasAccessor} instance
     * @param tarantoolCustomConversions a {@link TarantoolCustomConversions} instance
     * @return an {@link MappingTarantoolConverter} instance
     * @see #customConversions()
     */
    @Bean("mappingTarantoolConverter")
    public MappingTarantoolConverter mappingTarantoolConverter(TarantoolMappingContext tarantoolMappingContext,
                                                               TarantoolMapTypeAliasAccessor typeAliasAccessor,
                                                               TarantoolCustomConversions tarantoolCustomConversions) {
        return new MappingTarantoolConverter(tarantoolMappingContext, typeAliasAccessor, tarantoolCustomConversions);
    }

    /**
     * Creates a {@link TarantoolMapTypeAliasAccessor} instance for retrieving the serialized object type from
     * nested maps
     *
     * @return a {@link TarantoolMapTypeAliasAccessor} instance
     */
    @Bean("typeAliasAccessor")
    public TarantoolMapTypeAliasAccessor typeAliasAccessor() {
        return new TarantoolMapTypeAliasAccessor(TarantoolTupleTypeMapper.DEFAULT_TYPE_KEY);
    }

    /**
     * Creates a {@link TarantoolMappingContext} equipped with entity classes scanned from the mapping base package.
     *
     * @return TarantoolMappingContext instance
     * @throws ClassNotFoundException if the entity scan fails
     * @see #getMappingBasePackages()
     */
    @Bean("tarantoolMappingContext")
    public TarantoolMappingContext tarantoolMappingContext() throws ClassNotFoundException {

        TarantoolMappingContext mappingContext = new TarantoolMappingContext();
        mappingContext.setInitialEntitySet(getInitialEntitySet());
        mappingContext.setSimpleTypeHolder(TarantoolSimpleTypes.HOLDER);
        mappingContext.setFieldNamingStrategy(fieldNamingStrategy());

        return mappingContext;
    }

    /**
     * Creates an instance of {@link TarantoolCustomConversions} for customizing the conversion from some Java objects
     * into the objects which have internal mapping in the driver (like primitive types, Tarantool tuples, etc)
     *
     * @return a {@link TarantoolCustomConversions} instance
     */
    @Bean("tarantoolCustomConversions")
    public TarantoolCustomConversions customConversions() {
        return new TarantoolCustomConversions(customConverters());
    }

    /**
     * Override this method for providing custom conversions
     *
     * @return list of custom conversions
     */
    protected List<?> customConverters() {
        return Collections.emptyList();
    }

    /**
     * Creates the default driver-to-Spring exception translator
     *
     * @return new exception translator instance
     */
    @Bean
    public TarantoolExceptionTranslator tarantoolExceptionTranslator() {
        return new DefaultTarantoolExceptionTranslator();
    }

    /**
     * Creates the default query executors worker thread factory
     *
     * @return new factory instance
     */
    @Bean
    public ForkJoinWorkerThreadFactory queryExecutorsFactory() {
        return new WorkerFactory();
    }

    private static final class WorkerFactory implements ForkJoinWorkerThreadFactory {

        private final AtomicLong id = new AtomicLong();

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName("TarantoolTemplateQueryExecutor-" + id);
            return worker;
        }
    }
}
