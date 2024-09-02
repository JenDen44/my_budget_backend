package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ReportChart extends ReportItem {

    public ReportChart(Map<Category, Double> purchasesByCategory) {
        super(purchasesByCategory);
    }
}
