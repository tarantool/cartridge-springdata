package org.springframework.data.tarantool;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.TarantoolCartridgeContainer;
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
    private static final TarantoolCartridgeContainer tarantoolContainer =
        new TarantoolCartridgeContainer("cartridge/instances.yml", "cartridge/topology.lua")
            .withDirectoryBinding("cartridge")
            .withReuse(true)
            .withRouterPassword("testapp-cluster-cookie");

    @BeforeAll
    public static void setUp() throws Exception {
        tarantoolContainer.executeScript("test.lua").get();
    }

    @DynamicPropertySource
    static void tarantoolProperties(DynamicPropertyRegistry registry) {
        registry.add("tarantool.host", tarantoolContainer::getHost);
        registry.add("tarantool.port", tarantoolContainer::getPort);
        registry.add("tarantool.username", tarantoolContainer::getUsername);
        registry.add("tarantool.password", tarantoolContainer::getPassword);
    }
}
