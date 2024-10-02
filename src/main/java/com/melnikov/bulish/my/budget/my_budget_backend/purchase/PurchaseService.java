package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import org.springframework.stereotype.Service;

@Service
public interface PurchaseService {

    PurchaseDto findPurchaseById(Integer id);

    PurchaseDto savePurchase(PurchaseRequest purchase);

    PurchaseDto updatePurchase(PurchaseDto purchase, Integer id);

    void deletePurchase(Integer id);
}