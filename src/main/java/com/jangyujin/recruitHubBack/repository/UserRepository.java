package com.jangyujin.recruitHubBack.repository;

import com.jangyujin.recruitHubBack.dto.UserResponse;
import com.jangyujin.recruitHubBack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findByUserid(String userid);

    @Query(
            "SELECT new com.jangyujin.recruitHubBack.dto.UserResponse(u.userid, u.email) " +
            "FROM User u " +
            "WHERE u.username = :username AND u.phone = :phone"
    )
    Optional<UserResponse> findByUsernameAndPhone(@Param("username") String username, @Param("phone") String phone);

    //User findByUsername(String username);
}
