package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@Schema(description = "ReportTable Model Information")
public class ReportTable {

    @Schema(description = "Return date for group of purchases, with format yyyy-MM-dd", example = "2019-09-09")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @Schema(description = "Return map with purchases total grouped by Category", example = "CLOTHE, 1300")
    private Map<Category, Double> purchasesByCategory;

    public ReportTable(LocalDate date, Map<Category,Double> purchases) {
        this.purchasesByCategory = purchases;
        this.date = date;
    }
}