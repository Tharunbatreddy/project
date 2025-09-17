package com.example.proj.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class ApiResponseTest {
    @Test
    void testApiResponseBuilder() {
        // Arrange
        String code = "200";
        String message = "Success";
        Long teamId = 1L;
        String name = "Team A";
        String description = "This is team A";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now().plusHours(1);
        Integer teamSize = 5;
        List<String> messages = Arrays.asList("Message 1", "Message 2");

        // Act
        ApiResponse apiResponse = new ApiResponse.Builder(code, message)
                .teamId(teamId)
                .name(name)
                .description(description)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .teamSize(teamSize)
                .messages(messages)
                .build();

        // Assert
        assertNotNull(apiResponse);
        assertEquals(code, apiResponse.getCode());
        assertEquals(message, apiResponse.getMessage());
        assertEquals(teamId, apiResponse.getTeamId());
        assertEquals(name, apiResponse.getName());
        assertEquals(description, apiResponse.getDescription());
        assertEquals(createdAt, apiResponse.getCreatedAt());
        assertEquals(updatedAt, apiResponse.getUpdatedAt());
        assertEquals(teamSize, apiResponse.getTeamSize());
        assertEquals(messages, apiResponse.getMessages());
    }

    @Test
    void testApiResponseWithNullFields() {
        // Arrange
        String code = "200";
        String message = "Success";

        // Act
        ApiResponse apiResponse = new ApiResponse.Builder(code, message).build();

        // Assert
        assertNotNull(apiResponse);
        assertEquals(code, apiResponse.getCode());
        assertEquals(message, apiResponse.getMessage());
        assertNull(apiResponse.getData());
        assertNull(apiResponse.getTeamId());
        assertNull(apiResponse.getName());
        assertNull(apiResponse.getDescription());
        assertNull(apiResponse.getCreatedAt());
        assertNull(apiResponse.getUpdatedAt());
        assertNull(apiResponse.getTeamSize());
        assertNull(apiResponse.getMessages());
    }

    @Test
    void testApiResponseWithData() {
        // Arrange
        String code = "200";
        String message = "Success";
        String data = "Some data";

        ApiResponse apiResponse = new ApiResponse.Builder(code, message)
                .data(data)
                .build();

        // Assert
        assertNotNull(apiResponse);
        assertEquals(code, apiResponse.getCode());
        assertEquals(message, apiResponse.getMessage());
        assertEquals(data, apiResponse.getData());
    }

    @Test
    void testApiResponseWithTeamId() {

        String code = "200";
        String message = "Success";
        Long teamId = 1L;
        ApiResponse apiResponse = new ApiResponse.Builder(code, message)
                .teamId(teamId)
                .build();

        assertNotNull(apiResponse);
        assertEquals(teamId, apiResponse.getTeamId());
    }

    @Test
    void testApiResponseMessages() {
        // Arrange
        String code = "200";
        String message = "Success";
        List<String> messages = Arrays.asList("Message 1", "Message 2");

        // Act
        ApiResponse apiResponse = new ApiResponse.Builder(code, message)
                .messages(messages)
                .build();

        // Assert
        assertNotNull(apiResponse);
        assertEquals(messages, apiResponse.getMessages());
    }
}
