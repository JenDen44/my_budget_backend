package com.melnikov.bulish.my.budget.my_budget_backend.notification;

import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseDto;

public class CreatePurchaseNotification extends AbstractNotification<PurchaseDto> {
    public CreatePurchaseNotification(PurchaseDto entity) {
        super("PURCHASE", NotificationStatus.CREATED, entity);
    }
}