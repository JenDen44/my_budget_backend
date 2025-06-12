package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ReportTable Model Information")
public class ReportTable {

    @Schema(description = "Date for purchases group", example = "2019-09-09", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "Category totals", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Category, BigDecimal> purchasesByCategory;
}