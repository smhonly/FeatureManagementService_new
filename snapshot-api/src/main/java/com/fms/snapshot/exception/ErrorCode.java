package com.fms.snapshot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SNAPSHOT_NOT_FOUND (HttpStatus.NOT_FOUND,            "SNAPSHOT_NOT_FOUND", "Snapshot not found for env/app"),
    INVALID_REQUEST    (HttpStatus.BAD_REQUEST,          "INVALID_REQUEST",    "Invalid request"),
    VALIDATION_FAILED  (HttpStatus.BAD_REQUEST,          "VALIDATION_FAILED",  "Request validation failed"),
    INTERNAL_ERROR     (HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",    "Internal server error");

    private final HttpStatus httpStatus;
    private final String     code;
    private final String     message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code       = code;
        this.message    = message;
    }
}
