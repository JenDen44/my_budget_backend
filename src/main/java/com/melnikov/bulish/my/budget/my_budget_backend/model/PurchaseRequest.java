package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "PurchaseRequest Model Information")
public class PurchaseRequest {

    @Schema(description = "Category can be chosen from : CLOTHE, EDUCATION, FOOD, ENTERTAINMENT", example = "CLOTHE")
    private Category category;

    @Schema(description = "Cost", example = "1200")
    private Double cost;

    @Schema(description = "Quantity of bought goods or services", example = "2")
    private Integer quantity;

    @Schema(description = "Purchase Date should be filled in yyyy-MM-dd format", example = "2019-09-09")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
}
