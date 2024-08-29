package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReportItemByDate {

    LocalDate date;
    Map<Category, Double> purchasesByCategory;
}