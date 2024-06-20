package com.zulkan.ewallet.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient Balance");
    }
}
