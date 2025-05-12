package com.melnikov.bulish.my.budget.my_budget_backend.entity;

import lombok.Getter;

@Getter
public enum Category {

    FOOD("FOOD"),
    CLOTHE("CLOTHE"),
    EDUCATION("EDUCATION"),
    ENTERTAINMENT("ENTERTAINMENT");

    private final String code;

    Category(String code) {
        this.code = code;
    }
}