package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ReportChart Model Information")
public class ReportChart {

    @Schema(description = "Category", example = "CLOTHING", requiredMode = Schema.RequiredMode.REQUIRED)
    private Category category;

    @Schema(description = "Total purchases by category", example = "5200.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal total;
}
