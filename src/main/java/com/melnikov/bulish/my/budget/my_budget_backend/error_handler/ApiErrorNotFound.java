package com.melnikov.bulish.my.budget.my_budget_backend.error_handler;

public class ApiErrorNotFound extends ApiError {

    public ApiErrorNotFound(String message) {
        super(404, message);
    }
}