package com.example.proj.exception;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionalHandlerTest {
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleTeamException() {
        String errorMessage = "Team not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        TeamException teamException = new TeamException(errorMessage, status);
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler.handleTeamException(teamException);
        assertEquals(status, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(status.value(), response.getBody().getStatus());
    }

    @Test
    void testHandleInvalidSearchException() {
        String errorMessage = "Invalid search criteria";
        InvalidSearchException invalidSearchException = new InvalidSearchException(errorMessage);
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler.handleInvalidSearchException(invalidSearchException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }

    @Test
    void testHandleValidationException() {
        FieldError fieldError1 = new FieldError("object", "field1", "must not be null");
        FieldError fieldError2 = new FieldError("object", "field2", "must be a valid email");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler.handleValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("field1: must not be null; field2: must be a valid email; ", response.getBody().getMessage());
    }
    @Test
    void testHandleGenericException() {
        Exception genericException = new Exception("Unexpected error occurred");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler.handleGenericException(genericException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error: Unexpected server error.", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }
}

