package com.example.proj.Controller;

import com.example.proj.controller.UserController;
import com.example.proj.entity.User;
import com.example.proj.exception.TeamException;
import com.example.proj.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@SpringBootTest

class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
     void testCreateUser() {
        User user = new User("John Doe", "johndoe@example.com");
        when(userService.createUser(Mockito.any(User.class))).thenReturn(user);
        ResponseEntity<User> response = userController.createUser(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("johndoe@example.com", response.getBody().getEmail());
    }

    @Test
    void testCreateUserConflict() {
        // Arrange
        User user = new User("John Doe", "johndoe@example.com");
        when(userService.createUser(Mockito.any(User.class)))
                .thenThrow(new TeamException("User with this email already exists.", HttpStatus.CONFLICT));
        ResponseEntity<User> response = userController.createUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Should return 400 for conflict
    }

    @Test
    void testCreateUserBadRequest() {
        User user = new User("Invalid User", "");
        when(userService.createUser(Mockito.any(User.class)))
                .thenThrow(new TeamException("Invalid user data", HttpStatus.BAD_REQUEST));
        ResponseEntity<User> response = userController.createUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
