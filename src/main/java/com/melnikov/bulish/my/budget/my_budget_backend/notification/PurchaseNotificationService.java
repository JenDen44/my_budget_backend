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
        DeletePurchaseNotification deleteNotification = new DeletePurchaseNotification(purchaseId);
        WebSocketPayload<DeletePurchaseNotification> payload = new WebSocketPayload<>(deleteNotification);
        sessionService.sendMessage(userId, payload);
    }
    public void sendNotificationForCreate(PurchaseDto purchase, Integer userId) {
        log.info("sendNotificationForCreate purchase id {}, user id {} ", purchase.getId(), userId);
        CreatePurchaseNotification createNotification = new CreatePurchaseNotification(purchase);
        WebSocketPayload<CreatePurchaseNotification> payload = new WebSocketPayload<>(createNotification);
        sessionService.sendMessage(userId, payload);
    }
    public void sendNotificationForUpdate(PurchaseDto purchase,  Integer userId) {
        log.info("sendNotificationForUpdate purchase id {}, user id {} ", purchase.getId(), userId);
        UpdatePurchaseNotification updateNotification = new UpdatePurchaseNotification(purchase);
        WebSocketPayload<UpdatePurchaseNotification> payload = new WebSocketPayload<>(updateNotification);
        sessionService.sendMessage(userId, payload);
    }
}