package com.example.proj.entity;

import jakarta.persistence.*;
import java.util.ArrayList;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @ManyToMany(mappedBy = "users")
    private List<Team> teams = new ArrayList<>(); // List of teams the user belongs to

    // Constructors
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    // Helper method to add a team to the user
    public void addTeam(Team team) {
        if (!teams.contains(team)) {
            teams.add(team);
            team.getUsers().add(this); // Ensure bidirectional relationship
        }
    }

    // Helper method to remove a team from the user
    public void removeTeam(Team team) {
        if (teams.contains(team)) {
            teams.remove(team);
            team.getUsers().remove(this); // Ensure bidirectional relationship
        }
    }
}

