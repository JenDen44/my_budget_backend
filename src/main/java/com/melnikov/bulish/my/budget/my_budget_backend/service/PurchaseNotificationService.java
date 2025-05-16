package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseDto;
import com.melnikov.bulish.my.budget.my_budget_backend.model.CreatePurchaseNotification;
import com.melnikov.bulish.my.budget.my_budget_backend.model.DeletePurchaseNotification;
import com.melnikov.bulish.my.budget.my_budget_backend.model.UpdatePurchaseNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseNotificationService {

    private final WebSocketSessionService sessionService;

    public void sendNotificationForDelete(Integer purchaseId, Integer userId) {
        log.info("sendNotificationForDelete purchase id {}, user id {} ", purchaseId, userId);
        sessionService.sendMessage(userId, new DeletePurchaseNotification(purchaseId));
    }

    public void sendNotificationForCreate(PurchaseDto purchase, Integer userId) {
        log.info("sendNotificationForCreate purchase id {}, user id {} ", purchase.getId(), userId);
        sessionService.sendMessage(userId, new CreatePurchaseNotification(purchase));
    }

    public void sendNotificationForUpdate(PurchaseDto purchase,  Integer userId) {
        log.info("sendNotificationForUpdate purchase id {}, user id {} ", purchase.getId(), userId);
        sessionService.sendMessage(userId, new UpdatePurchaseNotification(purchase));
    }
}