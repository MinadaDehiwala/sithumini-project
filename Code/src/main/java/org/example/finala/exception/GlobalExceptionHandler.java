package org.example.finala.exception;

import org.example.finala.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<APIResponse<String>> handleCustomException(CustomException e) {

        return new ResponseEntity<>(
                new APIResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "Error occurred",
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(
                new APIResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        errors
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<String>> handleException(Exception e) {

        return new ResponseEntity<>(
                new APIResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        e.getMessage()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}