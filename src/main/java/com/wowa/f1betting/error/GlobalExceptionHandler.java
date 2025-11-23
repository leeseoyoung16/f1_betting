package com.wowa.f1betting.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {

        ErrorCode code = e.getErrorCode();

        Map<String, Object> body = Map.of(
                "status", code.getStatus().value(),
                "error", code.name(),
                "message", code.getMessage()
        );

        return ResponseEntity.status(code.getStatus()).body(body);
    }
}
