package com.melnikov.bulish.my.budget.my_budget_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class GeneratedToken {
    private final String token;
    private final Instant expirationTime;
}
