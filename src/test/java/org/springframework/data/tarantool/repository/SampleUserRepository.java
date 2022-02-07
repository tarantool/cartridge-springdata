package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.SampleUser;

import java.util.List;
import java.util.Optional;


/**
 * @author Oleg Kuznetsov
 * @author Artyom Dubinin
 */
public interface SampleUserRepository extends TarantoolRepository<SampleUser, String> {
    @Query(function = "get_users_with_age_gt", output = TarantoolSerializationType.TUPLE)
    List<SampleUser> usersWithAgeGreaterThen(Integer age);

    @Query(function = "get_predefined_user", output = TarantoolSerializationType.AUTO)
    SampleUser predefinedUser();

    @Query(function = "get_age_by_name", output = TarantoolSerializationType.AUTO)
    Optional<Integer> getAgeByName(String name);
}
