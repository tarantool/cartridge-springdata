package org.springframework.data.tarantool;

import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolClientFactory;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.TarantoolCartridgeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Alexey Kuzin
 */
@SpringBootTest(classes = TestConfig.class)
public class BaseIntegrationTest {

    private static Logger logger  = LoggerFactory.getLogger(BaseIntegrationTest.class);

    protected static final TarantoolCartridgeContainer tarantoolContainer =
            new TarantoolCartridgeContainer("cartridge/instances.yml", "cartridge/topology.lua")
                    .withDirectoryBinding("cartridge")
                    .withRouterPassword("testapp-cluster-cookie")
                    .withLogConsumer(new Slf4jLogConsumer(logger))
                    .waitingFor(Wait.forLogMessage(".*Listening HTTP on.*", 2))
                    .withStartupTimeout(Duration.ofMinutes(2));

    @BeforeAll
    static void startContainer() {
        if (!tarantoolContainer.isRunning()) {
            tarantoolContainer.start();
        }

        waitUntilRolesConfigured();
    }

    //FIXME this code should be moved to testcontaineres library.
    // See https://github.com/tarantool/cartridge-java-testcontainers/issues/34
    private static boolean waitUntilNodeIsConfigured(int port, int timeoutSec) {
        final int DEFAULT_TIMEOUT = 5 * 1000;

        boolean initalized = false;
        int attempt = 0;
        int delay = 500;
        TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> client = TarantoolClientFactory.createClient()
                .withCredentials(tarantoolContainer.getUsername(), tarantoolContainer.getPassword())
                .withConnectTimeout(DEFAULT_TIMEOUT)
                .withReadTimeout(DEFAULT_TIMEOUT)
                .withRequestTimeout(DEFAULT_TIMEOUT)
                .withProxyMethodMapping()
                .withAddress(tarantoolContainer.getRouterHost(), tarantoolContainer.getMappedPort(port))
                .build();

        try {
            while (attempt * delay / 1000.0 < timeoutSec) {
                List<?> state = client.eval("return require('cartridge.confapplier').get_state()").get();
                Object result = state.get(0);
                if (result instanceof String && result.equals("RolesConfigured")) {
                    initalized = true;
                    break;
                }
                Thread.sleep(delay);
                ++attempt;
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("Got exception while waiting for RolesConfigured state of client: ", e);
        }
        return initalized;
    }

    private static void waitUntilRolesConfigured() {
        int INIT_TIMEOUT_SEC = 30;
        Assertions.assertTrue(waitUntilNodeIsConfigured(3301, INIT_TIMEOUT_SEC));
        Assertions.assertTrue(waitUntilNodeIsConfigured(3302, INIT_TIMEOUT_SEC));
    }

    @DynamicPropertySource
    static void tarantoolProperties(DynamicPropertyRegistry registry) {
        registry.add("tarantool.host", tarantoolContainer::getHost);
        registry.add("tarantool.port", tarantoolContainer::getPort);
        registry.add("tarantool.username", tarantoolContainer::getUsername);
        registry.add("tarantool.password", tarantoolContainer::getPassword);
    }
}
