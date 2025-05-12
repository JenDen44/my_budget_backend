package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ReportChart Model Information")
public class ReportChart {

    @Schema(description = "Return category : CLOTHE, EDUCATION, FOOD, ENTERTAINMENT", example = "CLOTHE")
    private Category category;

    @Schema(description = "Return total for all purchases by category", example = "5200")
    private Double total;
}
