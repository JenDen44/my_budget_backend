package com.melnikov.bulish.my.budget.my_budget_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    public ValidationException(String entity, String mesage) {
        super(String.format("%s %s", entity, mesage));
    }
}
