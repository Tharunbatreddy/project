package com.example.proj.Service;

import com.example.proj.entity.User;
import com.example.proj.exception.TeamException;
import com.example.proj.repository.UserRepository;
import com.example.proj.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initializes mocks
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setEmail("test1@example.com");
        testUser1.setName("Test User 1");

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setEmail("test2@example.com");
        testUser2.setName("Test User 2");
    }

    @Test
    void getUserById_Success() { // public is required for JUnit tests
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(testUser1));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals("Test User 1", result.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_ThrowsExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        TeamException exception = assertThrows(TeamException.class, () -> userService.getUserById(userId));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void createUser_Success() {
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setName("New User");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(testUser1);

        User createdUser = userService.createUser(newUser);

        assertNotNull(createdUser);
        assertEquals("Test User 1", createdUser.getName());
        verify(userRepository, times(1)).save(newUser);
    }
    @Test
    void createUser_ThrowsExceptionWhenEmailExists() {
        User newUser = new User();
        newUser.setEmail("test@example.com");
        newUser.setName("New User");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

        TeamException exception = assertThrows(TeamException.class, () -> userService.createUser(newUser));
        assertEquals("User with this email already exists.", exception.getMessage());
    }
    @Test
    void deleteUser_Success() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
   void deleteUser_ThrowsExceptionWhenUserNotFound() {

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        TeamException exception = assertThrows(TeamException.class, () -> userService.deleteUser(userId));
        assertEquals("User not found.", exception.getMessage());
    }
    @Test
   void getUsersByIds_Success() {
        List<Long> userIds = Arrays.asList(1L, 2L);
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(testUser1, testUser2));

        List<User> users = userService.getUsersByIds(userIds);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(testUser1));
        assertTrue(users.contains(testUser2));
    }

    @Test
    void getUsersByIds_SomeUsersNotFound() {
        List<Long> userIds = Arrays.asList(1L, 3L);
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(testUser1));

        TeamException exception = assertThrows(TeamException.class, () -> userService.getUsersByIds(userIds));

        assertEquals("Some users not found.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}