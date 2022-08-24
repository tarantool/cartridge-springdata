package org.springframework.data.tarantool;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;

public final class TestUtils {

    /**
     * Its helps for hacking problems with weak test containers in github CI
     *
     * @param callable      any function
     * @param attemptsCount attempts count for invoking function
     * @param millisToSleep time in millis for sleep on each attempt
     * @param <T>           desired type
     * @return any result of function invocation
     */
    @SneakyThrows
    public static <T> T invokeWithAttempts(Callable<T> callable, int attemptsCount, int millisToSleep) {
        T result = null;
        int counter = 0;
        while (counter < attemptsCount) {
            result = callable.call();
            if (result != null) {
                return result;
            }
            if (counter > 0) {
                Thread.sleep(millisToSleep);
            }
            counter++;
        }
        return result;
    }

    private TestUtils() {
    }
}
