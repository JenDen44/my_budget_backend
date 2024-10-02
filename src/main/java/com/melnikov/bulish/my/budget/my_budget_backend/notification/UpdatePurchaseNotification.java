package com.melnikov.bulish.my.budget.my_budget_backend.notification;

import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseDto;

public class UpdatePurchaseNotification extends AbstractNotification<PurchaseDto> {

    public UpdatePurchaseNotification(PurchaseDto entity) {
        super("PURCHASE", NotificationStatus.UPDATED, entity);
    }
}