package com.melnikov.bulish.my.budget.my_budget_backend.error_handler;

import java.util.List;

public class ApiErrorValidation extends ApiError {

    public ApiErrorValidation(List<ErrorField> fields) {
        super(422, "Validation Error", fields);
    }

    public ApiErrorValidation(String message) {
        super(422, message);
    }
}