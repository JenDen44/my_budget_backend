package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractEntity;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "purchases")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Builder
public class Purchase extends AbstractEntity {

    private Category category;

    @Min(value = 1, message = "cost can't be negative or 0")
    @NotNull(message = "cost can't be null")
    private Double cost;

    @Min(value = 1, message = "quantity  can't be less 1")
    @Max(value = 5000, message = "quantity  can't be more 5000")
    private Integer quantity;
    private Double totalCost;
    private LocalDate purchaseDate;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Purchase(Category category, Double cost, Integer quantity, LocalDate purchaseDate) {
        this.category = category;
        this.cost = cost;
        this.quantity = quantity;
        this.totalCost = cost * quantity;
        this.purchaseDate = purchaseDate;
    }

    public Purchase(PurchaseDto purchaseResponse) {
        this.id = purchaseResponse.getId();
        this.category = purchaseResponse.getCategory();
        this.cost = purchaseResponse.getCost();
        this.quantity = purchaseResponse.getQuantity();
        this.totalCost = cost * quantity;
        this.purchaseDate = purchaseResponse.getPurchaseDate();
    }

    public Purchase(Category category, Double cost, Integer quantity, Double totalCost, LocalDate purchaseDate, User user) {
        this.category = category;
        this.cost = cost;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.purchaseDate = purchaseDate;
        this.user = user;
    }
}