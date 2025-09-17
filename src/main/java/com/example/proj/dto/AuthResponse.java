package com.example.proj.dto;

import com.example.proj.utils.ApiResponse;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AuthResponse {
    private String token;
    public AuthResponse(String token) {
        this.token = token;
    }

    // Constructor for invalid login response
    public AuthResponse() {
        this.token = null;
    }

    // Method to convert to ApiResponse format
    public ApiResponse toApiResponse() {
        if (this.token != null) {
            return new ApiResponse.Builder("200", "Login successful")
                    .data(this.token)
                    .build();
        }
        return new ApiResponse.Builder("403", "Invalid login details")
                .build();
    }
}