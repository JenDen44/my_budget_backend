package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PurchaseService {

    public PurchaseResponse findPurchaseById(Integer id);

    public List<PurchaseResponse> findAllPurchases();

    public PurchaseResponse savePurchase(PurchaseRequest purchase);

    public PurchaseResponse updatePurchase(PurchaseResponse purchase, Integer id);

    public void deletePurchase(Integer id);
}