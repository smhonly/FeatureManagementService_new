package com.fms.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    FLAG_NOT_FOUND          (HttpStatus.NOT_FOUND,            "FLAG_NOT_FOUND",          "Flag not found"),
    FLAG_KEY_CONFLICT       (HttpStatus.CONFLICT,             "FLAG_KEY_CONFLICT",       "Flag key already exists"),
    INVALID_REQUEST         (HttpStatus.BAD_REQUEST,          "INVALID_REQUEST",         "Invalid request"),
    VALIDATION_FAILED       (HttpStatus.BAD_REQUEST,          "VALIDATION_FAILED",       "Request validation failed"),
    INTERNAL_ERROR          (HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",         "Internal server error");

    private final HttpStatus httpStatus;
    private final String     code;
    private final String     message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code       = code;
        this.message    = message;
    }
}
