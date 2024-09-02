package com.melnikov.bulish.my.budget.my_budget_backend.errorhandler;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
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
