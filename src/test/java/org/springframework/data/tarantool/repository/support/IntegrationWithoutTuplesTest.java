package org.springframework.data.tarantool.repository.support;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.entities.SampleUser;
import org.springframework.data.tarantool.entities.TestSpace;
import org.springframework.data.tarantool.repository.BookAsTestSpaceRepository;
import org.springframework.data.tarantool.repository.SampleUserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Oleg Kuznetsov
 */
@Tag("integration")
public class IntegrationWithoutTuplesTest extends BaseIntegrationTest {

    @Autowired
    private SampleUserRepository sampleUserRepository;

    @Autowired
    private BookAsTestSpaceRepository bookAsTestSpaceRepository;

    @BeforeAll
    public static void setUp() throws Exception {
        tarantoolContainer.executeScript("test_setup.lua").get();
    }


    @BeforeEach
    @SneakyThrows
    void setUpTest() {
        tarantoolContainer.executeScript("test_teardown.lua").join();
    }

    @AfterAll
    @SneakyThrows
    public static void tearDown() {
        tarantoolContainer.executeScript("test_teardown.lua").join();
    }

    @Test
    public void test_returningSampleUsers_shouldReturnCorrectly() {
        //given
        SampleUser firstUser = SampleUser.builder()
                .name("Nastya")
                .age(23)
                .build();
        SampleUser result = sampleUserRepository.save(firstUser);
        assertThat(result).isEqualTo(firstUser);

        SampleUser secondUser = SampleUser.builder()
                .name("freddie")
                .age(45)
                .build();
        result = sampleUserRepository.save(secondUser);
        assertThat(result).isEqualTo(secondUser);

        List<SampleUser> users = sampleUserRepository.usersWithAgeGreaterThen(25);
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0)).isEqualTo(secondUser);
    }

    @Test
    public void test_returningSampleUserAge_shouldWorkCorrectly() {
        //given
        SampleUser user = SampleUser.builder()
                .name("Nastya")
                .age(23)
                .build();
        SampleUser result = sampleUserRepository.save(user);
        assertThat(result).isEqualTo(user);

        Optional<Integer> age = sampleUserRepository.getAgeByName(user.getName());
        assertTrue(age.isPresent());
        assertThat(age.get()).isEqualTo(user.getAge());
    }

    @Test
    public void test_returningNonExistSampleUserAge_shouldWorkCorrectly() {
        Optional<Integer> age = sampleUserRepository.getAgeByName("non-exist-user-name");
        assertFalse(age.isPresent());
    }

    @Test
    public void test_getPredefinedUser_shouldReturnCorrectly() {
        //given
        SampleUser user = SampleUser.builder()
                .name("John")
                .age(46)
                .build();

        SampleUser result = sampleUserRepository.predefinedUser();

        //then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void test_save_shouldSaveAndReturnBook_ifTestSpaceIsAClassName() {
        //given
        TestSpace entity = TestSpace.builder()
                .id(111)
                .name("Tales")
                .uniqueKey("udf65")
                .author("Grimm Brothers")
                .year(1569)
                .build();

        //when
        TestSpace saved = bookAsTestSpaceRepository.save(entity);

        //then
        assertThat(saved).isEqualTo(entity);
    }
}
