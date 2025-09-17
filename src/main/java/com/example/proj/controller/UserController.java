package com.example.proj.controller;

import com.example.proj.entity.User;
import com.example.proj.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(201).body(createdUser); // Return created user with 201 status
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(null); // Handle error with 400 status for bad request
        }
    }
}

