package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.NotificationStatus;

public class UpdatePurchaseNotification extends AbstractNotification<PurchaseDTO> {
    public UpdatePurchaseNotification(PurchaseDTO entity) {
        super("PURCHASE", NotificationStatus.UPDATED, entity);
    }
}