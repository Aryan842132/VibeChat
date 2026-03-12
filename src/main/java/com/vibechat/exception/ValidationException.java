package com.vibechat.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationException extends RuntimeException {

    private String message;

    public ValidationException(String message) {
        super(message);
        this.message = message;
    }
}
