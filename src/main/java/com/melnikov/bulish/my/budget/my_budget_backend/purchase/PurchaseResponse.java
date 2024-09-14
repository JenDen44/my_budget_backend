package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractDto;
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
@Schema(description = "PurchaseResponse Model Information")
public class PurchaseResponse extends AbstractDto {

    @Schema(description = "Category can be chosen from : CLOTHE, EDUCATION, FOOD, ENTERTAINMENT", example = "CLOTHE")
    private Category category;
    @Schema(description = "Cost", example = "1200")
    private Double cost;
    @Schema(description = "Quantity of bought goods or services", example = "2")
    private Integer quantity;
    @Schema(description = "Total Cost is calculable parameter , cost * quantity")
    private Double totalCost;
    @JsonIgnore
    private Integer userId;

    @Schema(description = "Purchase Date should be filled in yyyy-MM-dd format", example = "2019-09-09")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "purchaseDate  must be filled")
    private LocalDate purchaseDate;

    public PurchaseResponse(Category category, Double cost, Integer quantity, LocalDate purchaseDate) {
        this.category = category;
        this.cost = cost;
        this.quantity = quantity;
        this.totalCost = cost * quantity;
        this.purchaseDate = purchaseDate;
    }

    public PurchaseResponse(Purchase purchase) {
        this.id = purchase.getId();
        this.category = purchase.getCategory();
        this.cost = purchase.getCost();
        this.quantity = purchase.getQuantity();
        this.totalCost = cost * quantity;
        this.purchaseDate = purchase.getPurchaseDate();
        this.userId = purchase.getUser().getId();
    }
}