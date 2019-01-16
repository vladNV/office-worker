package com.org.worker.exception;

public class AuthenticationFailed extends RuntimeException {
    public AuthenticationFailed(String message) {
        super(message);
    }
}
