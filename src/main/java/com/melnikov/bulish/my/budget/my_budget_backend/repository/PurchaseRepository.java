package com.melnikov.bulish.my.budget.my_budget_backend.repository;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseSummaryDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {


    @Query("""
        SELECT new com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseSummaryDTO(p.cost, p.quantity, p.category, p.purchaseDate)
        FROM Purchase p
        WHERE p.purchaseDate BETWEEN :start AND :end
        AND p.user.id = :userId
        """)
    List<PurchaseSummaryDTO> findPurchaseSummariesByDateRange(
            @Param("start") LocalDate startDate,
            @Param("end") LocalDate endDate,
            @Param("userId") Long userId
            );

    @Transactional(readOnly = true)
    Page<Purchase> findByUserId(Long userId, Pageable pageable);
}