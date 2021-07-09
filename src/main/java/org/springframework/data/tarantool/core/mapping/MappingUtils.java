package org.springframework.data.tarantool.core.mapping;

import org.springframework.data.util.ParsingUtils;

class MappingUtils {

    static String camelCaseToSnakeCase(String string) {
        return ParsingUtils.reconcatenateCamelCase(string, "_");
    }

    public MappingUtils() throws Exception {
        throw new Exception("Couldn't create utility class!");
    }
}
