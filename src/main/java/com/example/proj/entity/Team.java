package com.example.proj.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer teamSize;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "team_users",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    // Default no-argument constructor
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            user.getTeams().add(this); // Ensure bidirectional relationship
        }
    }

    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
            user.getTeams().remove(this); // Ensure bidirectional relationship
        }
    }

    public void someFutureMethod() {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

}