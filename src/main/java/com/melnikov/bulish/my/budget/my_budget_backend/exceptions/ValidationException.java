package com.melnikov.bulish.my.budget.my_budget_backend.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String entity, String message) {
        super(String.format("%s %s", entity, message));
    }
}
