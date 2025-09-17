package com.example.proj.mapper;

import com.example.proj.dto.UserDTO;
import com.example.proj.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserMapperTest {
    private UserMappper userMappper;

    @BeforeEach
    void setUp() {
        userMappper = mock(UserMappper.class);
    }

    @Test
    void testToEntity() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("johndoe@example.com");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");

        when(userMappper.toEntity(userDTO)).thenReturn(user);  // Mock the method

        User mappedUser = userMappper.toEntity(userDTO);
        assertNotNull(mappedUser);
        assertEquals(1L, mappedUser.getId());
        assertEquals("John Doe", mappedUser.getName());
        assertEquals("johndoe@example.com", mappedUser.getEmail());
    }

    @Test
    void testToDTO() {
        User user = new User();
        user.setId(1L);
        user.setName("Jane Doe");
        user.setEmail("janedoe@example.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Jane Doe");
        userDTO.setEmail("janedoe@example.com");

        when(userMappper.toDTO(user)).thenReturn(userDTO);  // Mock the method

        UserDTO mappedUserDTO = userMappper.toDTO(user);
        assertNotNull(mappedUserDTO);
        assertEquals(1L, mappedUserDTO.getId());
        assertEquals("Jane Doe", mappedUserDTO.getName());
        assertEquals("janedoe@example.com", mappedUserDTO.getEmail());
    }
}
