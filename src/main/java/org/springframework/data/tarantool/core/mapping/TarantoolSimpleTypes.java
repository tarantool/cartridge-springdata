package org.springframework.data.tarantool.core.mapping;

import org.springframework.data.mapping.model.SimpleTypeHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Simple constant holder for a {@link SimpleTypeHolder} containing Java simple types acceptable by Tarantool
 *
 * @author Alexey Kuzin
 */
public abstract class TarantoolSimpleTypes {

    private static final Set<Class<?>> TARANTOOL_SIMPLE_TYPES;

    static {
        Set<Class<?>> simpleTypes = new HashSet<>();
        simpleTypes.add(boolean.class);
        simpleTypes.add(boolean[].class);
        simpleTypes.add(long.class);
        simpleTypes.add(long[].class);
        simpleTypes.add(short.class);
        simpleTypes.add(short[].class);
        simpleTypes.add(int.class);
        simpleTypes.add(int[].class);
        simpleTypes.add(byte.class);
        simpleTypes.add(byte[].class);
        simpleTypes.add(float.class);
        simpleTypes.add(float[].class);
        simpleTypes.add(double.class);
        simpleTypes.add(double[].class);
        simpleTypes.add(char.class);
        simpleTypes.add(char[].class);
        simpleTypes.add(Boolean.class);
        simpleTypes.add(Long.class);
        simpleTypes.add(Short.class);
        simpleTypes.add(Integer.class);
        simpleTypes.add(Byte.class);
        simpleTypes.add(Float.class);
        simpleTypes.add(Double.class);
        simpleTypes.add(Character.class);
        simpleTypes.add(String.class);
        simpleTypes.add(Class.class);
        simpleTypes.add(Byte[].class);
        simpleTypes.add(UUID.class);
        simpleTypes.add(BigDecimal.class);

        TARANTOOL_SIMPLE_TYPES = Collections.unmodifiableSet(simpleTypes);
    }

    public static final SimpleTypeHolder HOLDER = new SimpleTypeHolder(TARANTOOL_SIMPLE_TYPES, false);
}
