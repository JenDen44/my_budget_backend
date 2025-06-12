package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.NotificationStatus;

public class DeletePurchaseNotification extends AbstractNotification<Long> {
    public DeletePurchaseNotification(Long entityID) {
        super("PURCHASE", NotificationStatus.DELETED, entityID);
    }
}