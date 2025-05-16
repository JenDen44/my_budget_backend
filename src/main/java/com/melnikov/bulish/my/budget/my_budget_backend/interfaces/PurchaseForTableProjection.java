package com.melnikov.bulish.my.budget.my_budget_backend.interfaces;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;

import java.time.LocalDate;

public interface PurchaseForTableProjection {
    Double getTotalCost();
    Category getCategory();
    LocalDate getPurchaseDate();
}
