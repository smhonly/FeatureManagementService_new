package com.fms.management.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Map<String, Object>> handleBiz(BizException ex) {
        return body(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "detail", e.getDefaultMessage()))
                .toList();
        Map<String, Object> body = baseBody(ErrorCode.VALIDATION_FAILED, "Request validation failed");
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnknown(Exception ex) {
        log.error("Unhandled exception", ex);
        return body(ErrorCode.INTERNAL_ERROR, ex.getMessage());
    }

    private static ResponseEntity<Map<String, Object>> body(ErrorCode ec, String message) {
        return ResponseEntity.status(ec.getHttpStatus()).body(baseBody(ec, message));
    }

    private static Map<String, Object> baseBody(ErrorCode ec, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    ec.getHttpStatus().value());
        body.put("error",     ec.getHttpStatus().getReasonPhrase());
        body.put("code",      ec.getCode());
        body.put("message",   message);
        return body;
    }
}
