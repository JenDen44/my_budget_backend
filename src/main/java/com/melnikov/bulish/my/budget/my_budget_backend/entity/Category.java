package com.melnikov.bulish.my.budget.my_budget_backend.entity;

public enum Category {

    FOOD("FOOD"),
    CLOTHE("CLOTHE"),
    EDUCATION("EDUCATION"),
    ENTERTAINMENT("ENTERTAINMENT");

    private String code;

    private Category(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}