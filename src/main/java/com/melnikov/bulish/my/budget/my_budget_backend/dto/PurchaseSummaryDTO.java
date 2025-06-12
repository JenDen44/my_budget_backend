package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
public class PurchaseSummaryDTO {
    private final BigDecimal cost;
    private final Integer quantity;
    private final Category category;
    private final LocalDate purchaseDate;
}
