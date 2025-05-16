package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.NotificationStatus;

public class CreatePurchaseNotification extends AbstractNotification<PurchaseDto> {

    public CreatePurchaseNotification(PurchaseDto entity) {
        super("PURCHASE", NotificationStatus.CREATED, entity);
    }
}