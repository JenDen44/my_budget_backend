package com.melnikov.bulish.my.budget.my_budget_backend.exception;

public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(String message) {
        super(message);
    }
}