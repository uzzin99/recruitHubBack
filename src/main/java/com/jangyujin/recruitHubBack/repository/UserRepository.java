package com.jangyujin.recruitHubBack.repository;

import com.jangyujin.recruitHubBack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    //User findByUsername(String username);
}
