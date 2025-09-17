package com.example.proj.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Check if the exception is due to invalid credentials (login failed)
        if (authException instanceof BadCredentialsException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 status for Unauthorized
            response.setContentType("application/json");

            // Return the custom error message for invalid login details
            String errorMessage = "{\"code\": \"401\", \"message\": \"Invalid login details\"}";
            response.getWriter().write(errorMessage);
        } else {
            // For any other authentication-related issues (e.g., missing token), return 403 Forbidden
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 status for Forbidden
            response.setContentType("application/json");

            // Return the custom error message for unauthorized access
            String errorMessage = "{\"code\": \"403\", \"message\": \"Authentication required\"}";
            response.getWriter().write(errorMessage);
        }
    }
}