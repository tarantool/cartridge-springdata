package org.springframework.data.tarantool.repository;

import org.springframework.data.tarantool.entities.SampleUser;

import java.util.List;


/**
 * @author Oleg Kuznetsov
 */
public interface SampleUserRepository extends TarantoolRepository<SampleUser, String> {
    @Query(function = "returning_sample_user_object")
    SampleUser returningSampleUserObject(String name);

    @Query(function = "get_predefined_users")
    List<SampleUser> getPredefinedUsers();
}
