package com.org.worker.exception;

import lombok.Getter;

@Getter
public class LimitExceededException extends RuntimeException {
    private long actualSize;
    private long permittedSize;

    public LimitExceededException(String message, long actualSize, long permittedSize) {
        super(message);
        this.actualSize = actualSize;
        this.permittedSize = permittedSize;
    }

    @Override
    public String getMessage() {
        return super.getMessage() +
                "\n Actual size is " + actualSize +
                ", allowed size is " + permittedSize;
    }
}
