package com.melnikov.bulish.my.budget.my_budget_backend.error_handler;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private Integer code;
    private String message;
    private List<ErrorField> fields;


    public ApiError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
