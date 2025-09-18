package com.java.credit_simulator.controller;

import com.java.credit_simulator.model.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<WebResponse<String>> illegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder().errors(exception.getMessage()).build());
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<WebResponse<String>> runtimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(WebResponse.<String>builder().errors(exception.getMessage()).build());
    }
}
