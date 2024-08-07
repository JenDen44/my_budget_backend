package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

public class PurchaseNotFoundException extends RuntimeException{
    public PurchaseNotFoundException(String message) {
        super(message);
    }
}