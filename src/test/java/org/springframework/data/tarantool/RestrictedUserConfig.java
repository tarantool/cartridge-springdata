package org.springframework.data.tarantool;

import io.tarantool.driver.auth.SimpleTarantoolCredentials;
import io.tarantool.driver.auth.TarantoolCredentials;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.tarantool.repository.BookRepository;
import org.springframework.data.tarantool.repository.config.EnableTarantoolRepositories;

/**
 * @author Artyom Dubinin
 */
@Configuration
@EnableTarantoolRepositories(basePackageClasses = {BookRepository.class})
@EnableAutoConfiguration
public class RestrictedUserConfig extends BaseConfig {

    protected String username = "restricted_user";
    protected String password = "restricted_secret";

    @Override
    public TarantoolCredentials tarantoolCredentials() {
        return new SimpleTarantoolCredentials(username, password);
    }
}
