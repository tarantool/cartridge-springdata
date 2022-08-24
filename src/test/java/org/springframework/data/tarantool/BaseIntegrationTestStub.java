package org.springframework.data.tarantool;

import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.tarantool.repository.TarantoolRepository;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * A stub class for debugging with localhost cluster.
 * <p>
 * Inherit this class instead of {@link BaseIntegrationTest} to debug
 * your test without invoking testcontainers.
 *
 * @author Vladimir Rogach
 * @author Artyom Dubinin
 */
@SpringBootTest(classes = TestConfig.class,
        properties = {
                "tarantool.host=localhost",
                "tarantool.port=3301",
                "tarantool.username=admin",
                "tarantool.password=testapp-cluster-cookie"
        })
public class BaseIntegrationTestStub {

    private final Logger logger = LoggerFactory.getLogger(BaseIntegrationTestStub.class);

    @Autowired
    private List<TarantoolRepository<?, ?>> availableRepos;

    @Autowired
    private TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> client;

    protected final static TarantoolContainerStub tarantoolContainer = new TarantoolContainerStub();

    @PostConstruct
    private void initStub() {
        // wipe all data in cluster before running tests
        availableRepos
                .forEach(TarantoolRepository::deleteAll);

        tarantoolContainer.setClient(this.client);
        tarantoolContainer.runDeferred();
        logger.info("Container STUB initialized.");
    }

    /**
     * This class mimics a tarantool testcontainer but works with a running cluster.
     */
    public static class TarantoolContainerStub {

        private final Logger logger = LoggerFactory.getLogger(BaseIntegrationTestStub.class);

        private TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> client;
        private final List<String> deferredScripts = new ArrayList<>();

        /**
         * Reads given resource file as a string.
         *
         * @param fileName path to the resource file
         * @return the file's contents
         * @throws IOException if read fails for any reason
         */
        String getResourceFileAsString(String fileName) throws IOException {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            try (InputStream is = classLoader.getResourceAsStream(fileName)) {
                if (is == null) {
                    return null;
                }
                try (InputStreamReader isr = new InputStreamReader(is);
                     BufferedReader reader = new BufferedReader(isr)) {
                    return reader.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            }
        }

        public CompletableFuture<List<?>> executeScript(String script) {
            if (client == null) {
                // Client is initialized only after DI is finished.
                // So it is null during static init phase.
                // We put init commands to a buffer and will execute it a bit later.
                deferredScripts.add(script);
                return CompletableFuture.completedFuture(Collections.singletonList(true));
            }
            try {
                String cmd = getResourceFileAsString(script);
                logger.info("Executing command from script " + script + ": " + cmd);
                return client.eval(cmd);
            } catch (IOException e) {
                logger.error("Error running script " + script, e);
                return CompletableFuture.completedFuture(Collections.singletonList(true));
            }
        }

        public void runDeferred() {
            deferredScripts.forEach(script -> {
                try {
                    executeScript(script).get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error running script " + script, e);
                }
            });
        }

        public void setClient(TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> client) {
            this.client = client;
        }
    }
}
