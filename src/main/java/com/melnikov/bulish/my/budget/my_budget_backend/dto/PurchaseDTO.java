package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.validation.CategoryDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "PurchaseDTO Model Information")
public class PurchaseDTO {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Category", example = "CLOTHING", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "category is required")
    @JsonDeserialize(using = CategoryDeserializer.class)
    private Category category;

    @Schema(description = "Cost", example = "1200.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @DecimalMin(value = "0.01", message = "Cost must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Cost must have up to 10 integer and 2 fraction digits")
    private BigDecimal cost;

    @Schema(description = "Quantity", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 500, message = "Quantity cannot exceed 500")
    private Integer quantity;

    @Schema(description = "Purchase Date", example = "2019-09-09", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Purchase Date is required")
    private LocalDate purchaseDate;

    @Schema(description = "Total cost", example = "1500.00")
    private BigDecimal totalCost;

    public PurchaseDTO(Purchase purchase) {
        this.id = purchase.getId();
        this.category = purchase.getCategory();
        this.cost = purchase.getCost();
        this.quantity = purchase.getQuantity();
        this.purchaseDate = purchase.getPurchaseDate();
        this.totalCost = purchase.getTotalCost();
    }

    public BigDecimal getTotalCost() {
        return cost.multiply(BigDecimal.valueOf(quantity));
    }
}