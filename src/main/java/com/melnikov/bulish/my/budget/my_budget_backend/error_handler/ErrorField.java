package com.melnikov.bulish.my.budget.my_budget_backend.error_handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorField {

    private String field;
    private String message;
}