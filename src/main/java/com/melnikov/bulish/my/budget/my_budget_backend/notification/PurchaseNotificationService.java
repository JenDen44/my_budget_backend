package com.melnikov.bulish.my.budget.my_budget_backend.notification;

import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PurchaseNotificationService {

    private final WebSocketSessionService sessionService;

    public PurchaseNotificationService(WebSocketSessionService sessionService) {
        this.sessionService = sessionService;
    }

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