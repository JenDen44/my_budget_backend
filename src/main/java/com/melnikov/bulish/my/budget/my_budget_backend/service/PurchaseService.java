package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.PagedResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseRequest;

public interface PurchaseService {

    PurchaseDTO findPurchaseDtoById(Long id);

    PurchaseDTO savePurchase(PurchaseRequest purchase);

    PurchaseDTO updatePurchase(PurchaseDTO purchase, Long id);

    void deletePurchase(Long id);

    PagedResponse<PurchaseDTO> getPurchasesForCurrentUser(int pageNo, int pageSize, String sortBy, String sortDir);
}