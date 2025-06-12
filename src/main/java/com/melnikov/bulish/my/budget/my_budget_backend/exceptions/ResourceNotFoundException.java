package com.melnikov.bulish.my.budget.my_budget_backend.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName) {
        super(String.format("%s not found with the given input data : '%s'", resourceName, fieldName));
    }

    public ResourceNotFoundException(String resourceName) {
        super(String.format("%s not found", resourceName));
    }
}
