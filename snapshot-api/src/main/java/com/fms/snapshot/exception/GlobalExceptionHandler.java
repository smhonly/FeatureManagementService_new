package com.fms.snapshot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler.
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Map<String, Object>> handleBiz(BizException ex) {
        return body(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(SystemErrorException.class)
    public ResponseEntity<Map<String, Object>> handleSystem(SystemErrorException ex) {
        log.error("System error [{}]", ex.getErrorCode().getCode(), ex);
        return body(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return body(ErrorCode.INTERNAL_ERROR, "internal error");
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
