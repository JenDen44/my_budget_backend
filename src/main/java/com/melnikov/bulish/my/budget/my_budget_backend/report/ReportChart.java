package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Schema(description = "ReportChart Model Information")
public class ReportChart {

    @Schema(description = "Return category : CLOTHE, EDUCATION, FOOD, ENTERTAINMENT", example = "CLOTHE")
    private Category category;
    @Schema(description = "Return total for all purchases by category", example = "5200")
    private Double total;

    public ReportChart(Category category, Double total) {
        this.category = category;
        this.total = total;
    }
}
