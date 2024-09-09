package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends CrudRepository<Purchase, Integer>, PagingAndSortingRepository<Purchase,Integer> {

    @Query("SELECT NEW com.melnikov.bulish.my.budget.my_budget_backend.purchase.Purchase(p.category, p.cost, p.quantity,"
            + "  p.purchaseDate)"
            + " FROM Purchase p WHERE p.purchaseDate BETWEEN ?1 AND ?2")
    public List<Purchase> findPurchaseWithTimeBetween(LocalDate startTime, LocalDate endTime);

    @Query("SELECT p FROM Purchase p WHERE p.user.id=?1")
    Page<Purchase> findByUserWithPagination(Integer userId, Pageable pageable);
}