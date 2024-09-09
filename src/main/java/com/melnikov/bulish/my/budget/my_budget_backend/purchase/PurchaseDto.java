package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractDto;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto extends AbstractDto {

    private Category category;
    private Double cost;
    private Integer quantity;
    private Double totalCost;
    private Integer userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    public PurchaseDto(Category category, Double cost, Integer quantity, LocalDate purchaseDate) {
        this.category = category;
        this.cost = cost;
        this.quantity = quantity;
        this.totalCost = cost * quantity;
        this.purchaseDate = purchaseDate;
    }

    public PurchaseDto(Purchase purchase) {
        this.id = purchase.getId();
        this.category = purchase.getCategory();
        this.cost = purchase.getCost();
        this.quantity = purchase.getQuantity();
        this.totalCost = cost * quantity;
        this.purchaseDate = purchase.getPurchaseDate();
        this.userId = purchase.getUser().getId();
    }
}