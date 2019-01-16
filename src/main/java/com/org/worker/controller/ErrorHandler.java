package com.org.worker.controller;

import com.org.worker.exception.AuthenticationFailed;
import com.org.worker.exception.FileApiError;
import com.org.worker.exception.AppValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class, FileApiError.class})
    public ResponseEntity<Object> internalError(Throwable throwable) {
        LOG.error("Internal error", throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(throwable.getMessage());
    }

    @ExceptionHandler(value = {AppValidationException.class})
    public ResponseEntity<Object> validationException(RuntimeException exception) {
        LOG.error("bad request error", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(value = {NotAcceptableStatusException.class})
    public ResponseEntity<Object> unsupportedFile(RuntimeException exception) {
        LOG.error("unsupported file error", exception);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exception.getMessage());
    }

    @ExceptionHandler(value = {AuthenticationFailed.class})
    public ResponseEntity<Object> authenticationFailed(RuntimeException exception) {
        LOG.error("auth failed error", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }
}
