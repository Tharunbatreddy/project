package com.example.proj.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvalidSearchExceptionTest {
    @Test
    void testConstructorWithMessageAndStatus() {
        String message = "Invalid search criteria";
        HttpStatus status = HttpStatus.NOT_FOUND;
        InvalidSearchException exception = new InvalidSearchException(message, status);
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testConstructorWithMessageOnly() {
        String message = "Invalid search criteria";
        InvalidSearchException exception = new InvalidSearchException(message);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()); // Default status
    }

    @Test
    void testGetStatus() {
        HttpStatus status = HttpStatus.FORBIDDEN;
        InvalidSearchException exception = new InvalidSearchException("Forbidden error", status);
        HttpStatus result = exception.getStatus();
        assertEquals(status, result);
    }

    @Test
    void testExceptionInheritance() {
        String message = "Invalid search criteria";
        InvalidSearchException exception = new InvalidSearchException(message);
        assertNotNull(exception);
        assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
    }
}

