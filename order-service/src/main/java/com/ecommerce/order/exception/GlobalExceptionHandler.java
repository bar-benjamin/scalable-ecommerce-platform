package com.ecommerce.order.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> handleEmptyCart(EmptyCartException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        if (ex.status() == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                            "Upstream resource not found"));
        }
        if (ex.status() == 409) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(HttpStatus.CONFLICT.value(),
                            "Insufficient stock for one or more items"));
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(HttpStatus.BAD_GATEWAY.value(),
                        "Upstream service error: " + ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> field_errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            field_errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                        "Validation failed", field_errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred"));
    }

    public static class ErrorResponse {

        private final int status;
        private final String message;
        private final Map<String, String> errors;
        private final Instant timestamp;

        public ErrorResponse(int status, String message) {
            this(status, message, null);
        }

        public ErrorResponse(int status, String message, Map<String, String> errors) {
            this.status    = status;
            this.message   = message;
            this.errors    = errors;
            this.timestamp = Instant.now();
        }

        public int getStatus()                { return status; }
        public String getMessage()            { return message; }
        public Map<String, String> getErrors(){ return errors; }
        public Instant getTimestamp()         { return timestamp; }
    }
}