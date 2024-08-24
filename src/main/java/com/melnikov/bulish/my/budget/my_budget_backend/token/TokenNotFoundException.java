package com.melnikov.bulish.my.budget.my_budget_backend.token;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}