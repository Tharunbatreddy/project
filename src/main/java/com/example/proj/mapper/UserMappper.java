package com.example.proj.mapper;
import com.example.proj.dto.UserDTO;
import com.example.proj.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMappper {
    UserMappper INSTANCE = Mappers.getMapper(UserMappper.class);

    // Map UserDTO to User entity
    User toEntity(UserDTO userDTO);

    // Map User entity to UserDTO
    UserDTO toDTO(User user);
}