package com.example.proj.repository;

import com.example.proj.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    // Finds a team by name
    Optional<Team> findByName(String name);

    List<Team> findByUsersId(Long userId);

    // Case-insensitive search by name or description
    Page<Team> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);
    Page<Team> findByDescriptionContaining(String description, Pageable pageable);
    boolean existsByNameAndIdNot(String name, Long id);

    // Find all teams with pagination
    Page<Team> findAll(Pageable pageable);
}