package org.springframework.data.tarantool.core;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.fieldnames.EntityChild;
import org.springframework.data.tarantool.entities.fieldnames.EntityParent;

import java.util.Collections;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class TarantoolTemplateTest extends BaseIntegrationTest {
    @Autowired
    TarantoolTemplate tarantoolTemplate;

    private static final EntityChild entity = new EntityChild();
    static {
        entity
            .setId(111)
            .setName("Tales")
            .setKey("udf65")
            .setAuthor("Grimm Brothers");
    }

    private static final EntityParent entityParent = new EntityParent();
    static {
        entityParent
                .setId(111)
                .setName("Tales")
                .setKey("udf65")
                .setAuthor("Grimm Brothers");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_fieldNames_shouldGettingCorrectly_withInheritance() {
        HashMap<String, ?> result =
            (HashMap<String, ?>) tarantoolTemplate.mapParameters(Collections.singletonList(entity)).get(0);

        assertTrue(result.containsKey("id"));
        assertTrue(result.containsKey("key_from_parent"));
        assertTrue(result.containsKey("name_from_child"));
        assertTrue(result.containsKey("author"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_fieldNames_shouldGettingCorrectly_withoutInheritance() {
        HashMap<String, ?> result
                = (HashMap<String, ?>) tarantoolTemplate.mapParameters(Collections.singletonList(entityParent)).get(0);

        assertTrue(result.containsKey("id"));
        assertTrue(result.containsKey("key_from_parent"));
        assertTrue(result.containsKey("name_from_parent"));
        assertTrue(result.containsKey("author"));
    }
}
