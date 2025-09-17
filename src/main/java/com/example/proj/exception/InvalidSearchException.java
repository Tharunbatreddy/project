package com.example.proj.exception;

import org.springframework.http.HttpStatus;

public class InvalidSearchException extends RuntimeException {
    private final HttpStatus status;

    public InvalidSearchException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public InvalidSearchException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;  // Default to 400 Bad Request
    }

    public HttpStatus getStatus() {
        return status;
    }

}
