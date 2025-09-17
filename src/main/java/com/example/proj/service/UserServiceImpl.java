package com.example.proj.service;

import com.example.proj.dto.UserDTO;
import com.example.proj.entity.User;
import com.example.proj.exception.TeamException;
import com.example.proj.mapper.UserMappper;
import com.example.proj.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl  implements UserService {
    private final UserRepository userRepository;
    private final UserMappper userMapper;  // Inject the MapStruct mapper

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMapper = UserMappper.INSTANCE; // Use the MapStruct generated instance
    }

    @Override
    public List<User> getUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new TeamException("Some users not found.", HttpStatus.NOT_FOUND);
        }
        return users;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new TeamException("User not found.", HttpStatus.NOT_FOUND));
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new TeamException("User with this email already exists.", HttpStatus.CONFLICT);
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new TeamException("User not found.", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    // Example method to convert User to UserDTO
    public UserDTO getUserDTO(Long userId) {
        User user = getUserById(userId);
        return userMapper.toDTO(user); // Use MapStruct to convert User to UserDTO
    }
    //JWT IMPLEMENTATION
    @Override
    public boolean isUserExistByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);  // Ensure case-insensitivity
        return user != null;  // Return true if user exists
    }

    // Example method to convert a list of User to UserDTO
    public List<UserDTO> getUserDTOs(List<Long> userIds) {
        List<User> users = getUsersByIds(userIds);
        return users.stream()
                .map(userMapper::toDTO) // Map each User to UserDTO
                .toList();
    }

}
