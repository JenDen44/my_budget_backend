package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.NotificationStatus;

public class CreatePurchaseNotification extends AbstractNotification<PurchaseDTO> {
    public CreatePurchaseNotification(PurchaseDTO entity) {
        super("PURCHASE", NotificationStatus.CREATED, entity);
    }
}