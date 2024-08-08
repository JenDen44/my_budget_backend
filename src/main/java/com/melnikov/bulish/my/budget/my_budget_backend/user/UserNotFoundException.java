package com.melnikov.bulish.my.budget.my_budget_backend.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
