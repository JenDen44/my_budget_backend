package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
public class ReportTable {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Map<Category, Double> purchasesByCategory;

    public ReportTable(LocalDate date, Map<Category,Double> purchases) {
        this.purchasesByCategory = purchases;
        this.date = date;
    }
}