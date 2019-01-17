package com.org.worker.exception;

public class ConvertingException extends RuntimeException {
    public ConvertingException(String message) {
        super(message);
    }

    public ConvertingException(Throwable cause) {
        super(cause);
    }
}
