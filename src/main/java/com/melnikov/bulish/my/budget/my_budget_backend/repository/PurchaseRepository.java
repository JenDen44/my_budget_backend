package com.melnikov.bulish.my.budget.my_budget_backend.repository;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.interfaces.PurchaseForTableProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends CrudRepository<Purchase, Integer>, PagingAndSortingRepository<Purchase, Integer> {

    @Query("""
           SELECT NEW com.melnikov.bulish.my.budget.my_budget_backend.interfaces.PurchaseForTableProjection(p.totalCost, p.category, p.purchaseDate)
           FROM Purchase p WHERE p.purchaseDate BETWEEN ?1 AND ?2 AND p.user.id = ?3
           """
            )
    List<PurchaseForTableProjection> findPurchaseSummariesByDateRange(LocalDate startDate, LocalDate endDate, Integer userId);

    Page<Purchase> findByUserId(Integer userId, Pageable pageable);
}