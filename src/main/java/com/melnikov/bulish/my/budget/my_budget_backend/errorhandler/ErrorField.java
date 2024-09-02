package com.melnikov.bulish.my.budget.my_budget_backend.errorhandler;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ErrorField {
    private String field;
    private String message;
}