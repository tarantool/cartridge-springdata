package org.springframework.data.tarantool;

import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import lombok.SneakyThrows;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.tarantool.repository.TarantoolRepository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.TarantoolCartridgeContainer;
import org.testcontainers.containers.TarantoolContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexey Kuzin
 */
@SpringBootTest(classes = TestConfig.class)
@Testcontainers
public class BaseIntegrationTest {

    private static Logger logger  = LoggerFactory.getLogger(BaseIntegrationTest.class);

    protected static final TarantoolCartridgeContainer tarantoolContainer =
            new TarantoolCartridgeContainer("cartridge/instances.yml", "cartridge/topology.lua")
                    .withDirectoryBinding("cartridge")
                    .withReuse(true)
                    .withRouterPassword("testapp-cluster-cookie")
                    .withLogConsumer(new Slf4jLogConsumer(logger));

    @BeforeAll
    static void startContainer() {
        if (!tarantoolContainer.isRunning()) {
            tarantoolContainer.start();
        }
    }

    @DynamicPropertySource
    static void tarantoolProperties(DynamicPropertyRegistry registry) {
        registry.add("tarantool.host", tarantoolContainer::getHost);
        registry.add("tarantool.port", tarantoolContainer::getPort);
        registry.add("tarantool.username", tarantoolContainer::getUsername);
        registry.add("tarantool.password", tarantoolContainer::getPassword);
    }
}
