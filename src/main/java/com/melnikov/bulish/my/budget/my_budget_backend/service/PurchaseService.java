package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseDto;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseRequest;

public interface PurchaseService {

    PurchaseDto findPurchaseDtoById(Integer id);

    PurchaseDto savePurchase(PurchaseRequest purchase);

    PurchaseDto updatePurchase(PurchaseDto purchase, Integer id);

    void deletePurchase(Integer id);
}