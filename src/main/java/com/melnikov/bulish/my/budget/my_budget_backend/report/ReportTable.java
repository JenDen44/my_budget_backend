package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportTable extends ReportItem {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public ReportTable(LocalDate date, Map<Category,Double> purchases) {
        super(purchases);
        this.date = date;
    }
}