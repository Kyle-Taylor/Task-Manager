package com.skillstorm.auth_service.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /*
     * Handles validation errors thrown when @Valid fails in controller methods.
     * Extracts the first validation error message and returns it in a structured ApiError response.
     * Response: 400 Bad Request with details about the validation error. 
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {
            
            String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField()  + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
            
            ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                message,
                request.getRequestURI()
            );

            return ResponseEntity.badRequest().body(error);
        }

        /*
         * Handles UserNotFoundException thrown when a user is not found in the system.
         * Extracts the exception message and returns it in a structured ApiError response.
         * Response: 404 Not Found with details about the missing user.
        */
        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ApiError> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {
                ApiError error = new ApiError(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "User Not Found",
                    ex.getMessage(),
                    request.getRequestURI()
                );

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        
        /*
         * Handles InvalidCredentialsException thrown when a user provides incorrect credentials.
         * Extracts the exception message and returns it in a structured ApiError response.
         * Response: 401 Unauthorized with details about the invalid credentials.
        */
        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {
                ApiError error = new ApiError(
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    ex.getMessage(),
                    request.getRequestURI()
                );

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

        /*
         * Handles runtime exceptions thrown during request processing.
         * Extracts the exception message and returns it in a structured ApiError response.
         * Response: 400 Bad Request with details about the runtime error.
        */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiError> handleRuntime(
            RuntimeException ex,
            HttpServletRequest request) {
                ApiError error = new ApiError(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    request.getRequestURI()
                );

                return ResponseEntity.badRequest().body(error);
            }
    /*
     * Handles all other exceptions not specifically handled by other methods.
     * Extracts the exception message and returns it in a structured ApiError response.
     * Response: 500 Internal Server Error with details about the unexpected error.
    */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobal(
        Exception ex,
        HttpServletRequest request) {
            ex.printStackTrace();

            ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI()
             );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}
