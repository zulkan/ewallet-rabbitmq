package com.zulkan.ewallet.config;

import com.zulkan.ewallet.exception.FailedUpdateDataException;
import com.zulkan.ewallet.exception.InsufficientBalanceException;
import com.zulkan.ewallet.exception.InvalidAmountException;
import com.zulkan.ewallet.exception.InvalidDestinationAccountException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static Map<String, String> getDefaultMessage(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    @ExceptionHandler(value
            = { Exception.class, FailedUpdateDataException.class})
    protected ResponseEntity<Object> genericExceptionHandler(
            RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getDefaultMessage(ex.getMessage()));
    }
    @ExceptionHandler(value
            = { BadRequestException.class, InvalidAmountException.class})
    protected ResponseEntity<Object> badRequestHandler(
            RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getDefaultMessage(ex.getMessage()));
    }

    @ExceptionHandler(value
            = { InvalidDestinationAccountException.class})
    protected ResponseEntity<Object> invalidDestinationHandler(
            RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getDefaultMessage(ex.getMessage()));
    }

    @ExceptionHandler(value
            = { InsufficientBalanceException.class})
    protected ResponseEntity<Object> insufficientBalanceHandler(
            RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(400)).body(getDefaultMessage(ex.getMessage()));
    }

}
