package com.melnikov.bulish.my.budget.my_budget_backend.notification;

public class DeletePurchaseNotification extends AbstractNotification<Integer> {

    public DeletePurchaseNotification(Integer entityID) {
        super("PURCHASE", NotificationStatus.DELETED, entityID);
    }
}