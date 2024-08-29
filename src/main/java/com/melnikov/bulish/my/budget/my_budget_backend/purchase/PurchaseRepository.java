package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase,Integer> {

    @Query("SELECT NEW com.melnikov.bulish.my.budget.my_budget_backend.purchase.Purchase(p.category, p.cost, p.quantity,"
            + "  p.purchaseDate)"
            + " FROM Purchase p WHERE p.purchaseDate BETWEEN ?1 AND ?2")
    public List<Purchase> findPurchaseWithTimeBetween(LocalDate startTime, LocalDate endTime);
}