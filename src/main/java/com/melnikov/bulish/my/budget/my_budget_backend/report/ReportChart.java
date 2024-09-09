package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ReportChart {

    private Category category;
    private Double total;

    public ReportChart(Category category, Double total) {
        this.category = category;
        this.total = total;
    }
}
