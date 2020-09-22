package org.springframework.data.tarantool;

import org.junit.ClassRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.TarantoolContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author Alexey Kuzin
 */
@SpringBootTest(classes = TestConfig.class)
@Testcontainers
public class BaseIntegrationTest {

    @ClassRule
    @Container
    private static final TarantoolContainer tarantoolContainer = new TarantoolContainer();

    @DynamicPropertySource
    static void tarantoolProperties(DynamicPropertyRegistry registry) {
        registry.add("tarantool.host", tarantoolContainer::getHost);
        registry.add("tarantool.port", tarantoolContainer::getPort);
        registry.add("tarantool.username", tarantoolContainer::getUsername);
        registry.add("tarantool.password", tarantoolContainer::getPassword);
    }
}
