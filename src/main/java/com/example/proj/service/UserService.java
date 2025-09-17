package com.example.proj.service;

import com.example.proj.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUsersByIds(List<Long> userIds); // method to retrieve users by their IDs
    User getUserById(Long userId); // method to get a user by their ID
    User createUser(User user); // method to create a user
    void deleteUser(Long userId);

    boolean isUserExistByEmail(String email);


}