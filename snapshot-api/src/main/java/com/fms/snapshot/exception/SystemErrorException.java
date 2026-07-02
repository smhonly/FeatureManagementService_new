package com.fms.snapshot.exception;

import lombok.Getter;

@Getter
public class SystemErrorException extends RuntimeException {

    private final ErrorCode errorCode;

    public SystemErrorException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SystemErrorException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
