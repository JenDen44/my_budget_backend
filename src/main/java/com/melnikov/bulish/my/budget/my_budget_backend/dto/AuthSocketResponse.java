package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthSocketResponse {
    private String authStatus;
    private Long userId;
}