package com.melnikov.bulish.my.budget.my_budget_backend.entity;

public enum Category {
    FOOD("Food"), CLOTHE("Clothe"), EDUCATION("Education"), ENTERTAINMENT("Entertainment");

    private String code;

    private Category(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}