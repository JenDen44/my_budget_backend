package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @NotNull(message = "purchaseDate  must be filled")
    private LocalDate purchaseDate;

}
