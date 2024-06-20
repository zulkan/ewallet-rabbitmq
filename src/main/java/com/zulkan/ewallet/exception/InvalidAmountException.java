package com.zulkan.ewallet.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
    public InvalidAmountException() {
        super("Invalid Amount Input");
    }
}
