package com.example.proj.controller;

import com.example.proj.dto.AuthRequest;
import com.example.proj.service.UserService;
import com.example.proj.utils.ApiResponse;
import com.example.proj.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        private  final JwtUtil jwtUtil;

        private  final UserService userService;

        @Autowired
        public AuthController(JwtUtil jwtUtil, UserService userService) {
            this.jwtUtil = jwtUtil;
            this.userService = userService;
        }

        // Endpoint for login
        @PostMapping("/login")
        public ResponseEntity<ApiResponse> authenticateUser(@RequestBody AuthRequest authRequest) {
            String email = authRequest.getEmail();  // Get email from AuthRequest DTO

            // Check if the user exists by email
            if (userService.isUserExistByEmail(email)) {
                // Generate the JWT token
                String token = jwtUtil.generateToken(email);

                // Return the generated token in the response with success status
                ApiResponse response = new ApiResponse.Builder("200", "Login successful")
                        .data(token)  // Add the JWT token as data
                        .build();
                return ResponseEntity.ok(response);  // 200 OK with token in response
            } else {
                // Return a 403 status with custom error message if the user is not found
                ApiResponse response = new ApiResponse.Builder("403", "Invalid login details")
                        .build();  // No data in case of error
                return ResponseEntity.status(403).body(response);  // 403 Forbidden
            }
        }
        // Optional: Logout functionality (if session-based authentication is used)
        @PostMapping("/logout")
        public ResponseEntity<String> logoutUser() {
            // Invalidate JWT (Optional, depending on your security setup)
            return ResponseEntity.ok("Successfully logged out");
        }
    }
