package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.NotificationStatus;

public class UpdatePurchaseNotification extends AbstractNotification<PurchaseDto> {

    public UpdatePurchaseNotification(PurchaseDto entity) {
        super("PURCHASE", NotificationStatus.UPDATED, entity);
    }
}