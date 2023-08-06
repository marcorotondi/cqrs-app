package com.marco.cqrs.exception;

public class InvalidFlowException extends RuntimeException

{

    public InvalidFlowException() {
        super();
    }

    public InvalidFlowException(String message) {
        super(message);
    }

    public InvalidFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFlowException(Throwable cause) {
        super(cause);
    }

    protected InvalidFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}