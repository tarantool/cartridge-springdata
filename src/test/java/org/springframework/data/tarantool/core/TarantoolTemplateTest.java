package org.springframework.data.tarantool.core;

import io.tarantool.driver.api.conditions.Conditions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.Address;
import org.springframework.data.tarantool.entities.Book;
import org.springframework.data.tarantool.entities.BookNonEntity;
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

    private static Customer vasya = Customer.builder()
            .id(1L)
            .name("Vasya")
            .tags(Arrays.asList("one", "two"))
            .addresses(generateAddresses())
            .foreignAddresses(generateForeignAddresses())
            .lastVisitTime(LocalDateTime.now())
            .build();
    private static Customer petya = Customer.builder()
            .id(2L)
            .name("Petya")
            .tags(Arrays.asList("one", "two"))
            .addresses(generateAddresses())
            .foreignAddresses(generateForeignAddresses())
            .lastVisitTime(LocalDateTime.now())
            .build();
    private static Customer tanya = Customer.builder()
            .id(3L)
            .name("Tanya")
            .tags(Arrays.asList("one", "two"))
            .foreignAddresses(generateForeignAddresses())
            .lastVisitTime(LocalDateTime.now())
            .build();

    private static Book book  = Book.builder()
            .id(4)
            .name("Tales")
            .uniqueKey("udf65")
            .author("Grimm Brothers")
            .year(1569)
            .build();

    private static BookNonEntity bookNonEntity  = BookNonEntity.builder()
            .id(4)
            .name("Tales")
            .uniqueKey("udf65")
            .author("Grimm Brothers")
            .year(1569)
            .issuerAddress(Address.builder().city("Riga").street("Brivibas").number(13).build())
            .storeAddresses(Collections.singletonList(Address.builder().city("Riga").street("Brivibas").number(13).build()))
            .build();

    @BeforeAll
    private static void setUp() {
        vasya.setFavouriteBooks(Collections.singletonList(book));
        petya.setFavouriteBooks(Collections.singletonList(book));
        tanya.setFavouriteBooks(Collections.singletonList(book));
    }

    private static Map<String, Address> generateAddresses() {
        return Collections.singletonMap("home", Address.builder().city("Moscow").street("Lubyanka").number(13).build());
    }

    private static List<Address> generateForeignAddresses() {
        return Collections.singletonList(Address.builder().city("Riga").street("Brivibas").number(13).build());
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
                () -> assertEquals("Tales", all.get(0).getName()),
                () -> assertEquals(Arrays.asList("one", "two"), all.get(0).getTags()),
                () -> assertTrue(all.get(0).getLastVisitTime().isBefore(LocalDateTime.now())),
                () -> assertEquals("Moscow", all.get(0).getAddresses().get("home").getCity()),
                () -> assertEquals("Riga", all.get(0).getForeignAddresses().get(0).getCity()),
                () -> assertTrue(all.get(0).getFavouriteBooks().size() > 0)
        );
    }

    @Test
    void testSelectNonEntity() {
        BookNonEntity newBook = tarantoolOperations.call("crud.replace_object", Arrays.asList("test_space", bookNonEntity), BookNonEntity.class);
        List<BookNonEntity> all = tarantoolOperations.callForList("crud.select", Collections.singletonList("test_space"), BookNonEntity.class);
        assertEquals(1, all.size());
        assertAll(
                () -> assertEquals("Vasya", all.get(0).getName()),
                () -> assertEquals("Brivibas", all.get(0).getIssuerAddress().getStreet()),
                () -> assertEquals("Riga", all.get(0).getStoreAddresses().get(0).getCity())
        );
    }

    @Test
    public void testFunctionAcceptingObject() {
        List<Customer> byCity = tarantoolOperations.callForList("find_customer_by_address",
                new Address[]{vasya.getAddresses().get("home")}, Customer.class);
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
    void testReadNullInNestedObject() {
        Customer zhuzha = Customer.builder()
                .id(4L)
                .name("Tanya")
                .tags(Arrays.asList("one", "two"))
                .addresses(null)
                .lastVisitTime(LocalDateTime.now())
                .build();

        Customer zhuzhaSaved = tarantoolOperations.save(zhuzha, Customer.class);
        Customer zhuzhaNew = tarantoolOperations.findById(4, Customer.class);
        assertEquals(null, zhuzhaNew.getAddresses());
        assertEquals(null, zhuzhaNew.getWorkAddress());
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

    @Test
    void testNonEntityAsReturnType() {
        List<Address> addresses = tarantoolOperations.callForList("get_customer_addresses", Address.class);
        assertTrue(addresses != null && addresses.size() > 0);
    }

    @Test
    void testNonEntityAsReturnType_shouldHandleError() {
        Throwable ex = null;
        try {
            tarantoolOperations.callForList("returning_error", Address.class);
        } catch (DataRetrievalFailureException e) {
            ex = e;
            assertTrue(e.getMessage().contains("some error"));
        }
        assertNotNull(ex);
    }

    @Test
    void testNonEntityAsReturnType_shouldHandleNil() {
        List<Address> addresses = tarantoolOperations.callForList("returning_nil", Address.class);
        assertNull(addresses);
        List<Customer> customers = tarantoolOperations.callForList("returning_nil", Customer.class);
        assertNull(customers);
        Address address = tarantoolOperations.call("returning_nil", Address.class);
        assertNull(address);
        Customer customer = tarantoolOperations.call("returning_nil", Customer.class);
        assertNull(customer);
        assertDoesNotThrow(() ->tarantoolOperations.call("returning_nil", Address.class));
        assertDoesNotThrow(() ->tarantoolOperations.call("returning_nil", Customer.class));
    }

    @Test
    void testNonEntityAsReturnType_shouldHandleNothing() {
        List<Address> addresses = tarantoolOperations.callForList("returning_nothing", Address.class);
        assertNull(addresses);
        List<Customer> customers = tarantoolOperations.callForList("returning_nothing", Customer.class);
        assertNull(customers);
        Address address = tarantoolOperations.call("returning_nothing", Address.class);
        assertNull(address);
        Customer customer = tarantoolOperations.call("returning_nothing", Customer.class);
        assertNull(customer);
        assertDoesNotThrow(() ->tarantoolOperations.call("returning_nothing", Address.class));
        assertDoesNotThrow(() ->tarantoolOperations.call("returning_nothing", Customer.class));
    }
}