package org.springframework.data.tarantool.core;

import io.tarantool.driver.api.conditions.Conditions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.Address;
import org.springframework.data.tarantool.entities.Customer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alexey Kuzin
 */
@Tag("integration")
class TarantoolTemplateTest extends BaseIntegrationTest {
    @Autowired
    TarantoolOperations tarantoolOperations;

//    private Customer vasya = Customer.builder().uuid(UUID.randomUUID()).name("Vasya").tags(Arrays.asList("one", "two")).build();
//    private Customer petya = Customer.builder().uuid(UUID.randomUUID()).name("Petya").tags(Arrays.asList("one", "two")).build();
//    private Customer tanya = Customer.builder().uuid(UUID.randomUUID()).name("Tanya").tags(Arrays.asList("one", "two")).build();
    private Customer vasya = Customer.builder()
        .id(1L)
        .name("Vasya")
        .tags(Arrays.asList("one", "two"))
        .addresses(generateAddresses())
        .lastVisitTime(LocalDateTime.now())
        .build();
    private Customer petya = Customer.builder()
        .id(2L)
        .name("Petya")
        .tags(Arrays.asList("one", "two"))
        .addresses(generateAddresses())
        .lastVisitTime(LocalDateTime.now())
        .build();
    private Customer tanya = Customer.builder()
        .id(3L)
        .name("Tanya")
        .tags(Arrays.asList("one", "two"))
        .addresses(generateAddresses())
        .lastVisitTime(LocalDateTime.now())
        .build();

    private Map<String, Address> generateAddresses() {
        return Collections.singletonMap("home", Address.builder().city("Moscow").street("Lubyanka").number(13).build());
    }

    @BeforeEach
    void setUpTest() {
        tarantoolOperations.findAndRemove(Conditions.any(), Customer.class);

        tarantoolOperations.save(vasya, Customer.class);
        tarantoolOperations.save(petya, Customer.class);
        tarantoolOperations.save(tanya, Customer.class);
    }

    @Test
    void testSelect() {
        List<Customer> all = tarantoolOperations.findAll(Customer.class);
        assertEquals(3, all.size());
        assertAll(
                () -> assertEquals("Vasya", all.get(0).getName()),
                () -> assertEquals(Arrays.asList("one", "two"), all.get(0).getTags()),
                () -> assertTrue(all.get(0).getLastVisitTime().isBefore(LocalDateTime.now())),
                () -> assertEquals("Moscow", all.get(0).getAddresses().get("home").getCity())
        );
    }

//    @Test
//    void testUpdate() {
//        tarantoolOperations.update(Conditions.equals("name", "Vasya"), TupleOperations.set("balance", 100));
//    }
}