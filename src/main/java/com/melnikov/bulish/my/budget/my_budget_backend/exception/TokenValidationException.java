package com.melnikov.bulish.my.budget.my_budget_backend.exception;

public class TokenValidationException extends RuntimeException {

    public TokenValidationException(String message) {
        super(message);
    }
}
