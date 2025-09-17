package com.example.proj.repository;

import com.example.proj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Fetch users by a list of user IDs
    List<User> findAllById(Iterable<Long> ids);

    // Check if a user exists by email
    boolean existsByEmail(String email);
//JWT TOKEN
Optional<User> findByEmailIgnoreCase(String email);

}
