package com.zulkan.ewallet.exception;

public class InvalidDestinationAccountException extends RuntimeException{

    public InvalidDestinationAccountException(String message) {
        super(message);
    }
}
