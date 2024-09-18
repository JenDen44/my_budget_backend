package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PurchaseService {

    public PurchaseDto findPurchaseById(Integer id);

    public List<PurchaseDto> findAllPurchases();

    public PurchaseDto savePurchase(PurchaseRequest purchase);

    public PurchaseDto updatePurchase(PurchaseDto purchase, Integer id);

    public void deletePurchase(Integer id);
}