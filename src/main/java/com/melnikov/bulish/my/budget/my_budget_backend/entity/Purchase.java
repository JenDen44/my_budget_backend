package com.melnikov.bulish.my.budget.my_budget_backend.entity;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase extends AbstractEntity {

    @NotNull
    private Category category;

    @NotNull(message = "Cost is required")
    @Column(precision=12, scale=2)
    private BigDecimal cost;

    @NotNull
    private Integer quantity;

    @NotNull
    private LocalDate purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public BigDecimal getTotalCost() {
        return cost.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "category=" + category +
                ", cost=" + cost +
                ", quantity=" + quantity +
                ", purchaseDate=" + purchaseDate +
                ", id=" + id +
                '}';
    }
}