package com.melnikov.bulish.my.budget.my_budget_backend.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.melnikov.bulish.my.budget.my_budget_backend.validation.CategoryDeserializer;
import lombok.Getter;

@JsonDeserialize(using = CategoryDeserializer.class)
@Getter
public enum Category {
    FOOD("Food"),
    CLOTHING("Clothing"),
    EDUCATION("Education"),
    ENTERTAINMENT("Entertainment"),
    HOUSING("Housing"),
    TRANSPORTATION("Transportation"),
    HEALTHCARE("Healthcare"),
    UTILITIES("Utilities");

    private final String displayName;
    Category(String displayName) { this.displayName = displayName; }
}
