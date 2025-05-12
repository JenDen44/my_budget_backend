package com.melnikov.bulish.my.budget.my_budget_backend.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}