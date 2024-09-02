package com.melnikov.bulish.my.budget.my_budget_backend.errorhandler;

public class ApiErrorNotFound extends ApiError {
    public ApiErrorNotFound(String message) {
        super(404, message);
    }
}