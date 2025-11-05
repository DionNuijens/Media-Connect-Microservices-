package org.example.authservice.repository;

import org.example.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}