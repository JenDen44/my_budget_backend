package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractEntity;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "purchases")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Purchase extends AbstractEntity {

    private Category category;
    private Double cost;
    private Integer quantity;
    private Double totalCost;
    private LocalDate purchaseDate;

    public Purchase(Category category, Double cost, Integer quantity, LocalDate purchaseDate) {
        this.category = category;
        this.cost = cost;
        this.quantity = quantity;
        this.totalCost = cost * quantity;
        this.purchaseDate = purchaseDate;
    }

    public Purchase(PurchaseDto purchaseDto) {
        this.id = purchaseDto.getId();
        this.category = purchaseDto.getCategory();
        this.cost = purchaseDto.getCost();
        this.quantity = purchaseDto.getQuantity();
        this.totalCost = cost * quantity;
        this.purchaseDate = purchaseDto.getPurchaseDate();
    }
}