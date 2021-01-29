package org.springframework.data.tarantool.core;

import io.tarantool.driver.api.conditions.Conditions;
import io.tarantool.driver.api.tuple.operations.TupleOperations;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    public void testFunctionAcceptingObject() {
        List<Customer> byCity = tarantoolOperations.callForList("find_customer_by_address", new Address[]{vasya.getAddresses().get("home")}, Customer.class);
        assertTrue(byCity != null && byCity.size() > 0);
    }

    @Test
    void testUpdateOne() {
        Address newAddress = Address.builder().city("Vladimir").street("Moskovskaya").number(123).build();
        Customer vasyaUpdated = Customer.builder()
                .addresses(Collections.singletonMap("home", newAddress))
                .build();
        List<Customer> updates = tarantoolOperations.update(
                Conditions.equals("name", "Vasya"),
                vasyaUpdated, Customer.class);
        assertEquals("Vladimir", updates.get(0).getAddresses().get("home").getCity());

        Customer newVasya = tarantoolOperations.findById(1, Customer.class);
        assertEquals("Vladimir", newVasya.getAddresses().get("home").getCity());
    }

    @Test
    void testUpdateMany() {
        Address newAddress = Address.builder().city("Vladimir").street("Moskovskaya").number(123).build();
        Customer vasyaUpdated = Customer.builder()
                .addresses(Collections.singletonMap("home", newAddress))
                .build();
        List<Customer> updates = tarantoolOperations.update(
                Conditions.any(),
                vasyaUpdated, Customer.class);
        assertTrue(updates.size() > 0);
        for (Customer update : updates) {
            assertEquals("Vladimir", update.getAddresses().get("home").getCity());
            Customer updated = tarantoolOperations.findById(update.getId(), Customer.class);
            assertEquals("Vladimir", updated.getAddresses().get("home").getCity());
        }
    }
}