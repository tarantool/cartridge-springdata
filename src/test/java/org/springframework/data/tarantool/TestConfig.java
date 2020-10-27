package org.springframework.data.tarantool;

import io.tarantool.driver.ProxyTarantoolClient;
import io.tarantool.driver.TarantoolClientConfig;
import io.tarantool.driver.TarantoolClusterAddressProvider;
import io.tarantool.driver.TarantoolServerAddress;
import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.auth.SimpleTarantoolCredentials;
import io.tarantool.driver.auth.TarantoolCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.tarantool.config.AbstractTarantoolDataConfiguration;
import org.springframework.data.tarantool.repository.BookRepository;
import org.springframework.data.tarantool.repository.config.EnableTarantoolRepositories;

/**
 * @author Alexey Kuzin
 */
@Configuration
@EnableTarantoolRepositories(basePackageClasses = {BookRepository.class})
@EnableAutoConfiguration
public class TestConfig extends AbstractTarantoolDataConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AbstractTarantoolDataConfiguration.class);

    @Value("${tarantool.host}")
    protected String host;
    @Value("${tarantool.port}")
    protected int port;
    @Value("${tarantool.username}")
    protected String username;
    @Value("${tarantool.password}")
    protected String password;

    @Override
    protected void configureClientConfig(TarantoolClientConfig.Builder builder) {
        builder
            .withConnectTimeout(1000 * 5)
            .withReadTimeout(1000 * 5)
            .withRequestTimeout(1000 * 5);
    }

    @Override
    public TarantoolCredentials tarantoolCredentials() {
        return new SimpleTarantoolCredentials(username, password);
    }

    @Override
    protected TarantoolServerAddress tarantoolServerAddress() {
        return new TarantoolServerAddress(host, port);
    }

    @Override
    public TarantoolClient tarantoolClient(TarantoolClientConfig tarantoolClientConfig,
                                           TarantoolClusterAddressProvider tarantoolClusterAddressProvider) {
        return new ProxyTarantoolClient(super.tarantoolClient(tarantoolClientConfig, tarantoolClusterAddressProvider));
    }
}
