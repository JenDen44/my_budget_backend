package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.NotificationException;
import com.melnikov.bulish.my.budget.my_budget_backend.model.CreatePurchaseNotification;
import com.melnikov.bulish.my.budget.my_budget_backend.model.DeletePurchaseNotification;
import com.melnikov.bulish.my.budget.my_budget_backend.model.UpdatePurchaseNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseNotificationService {

    private final WebSocketSessionService sessionService;

    public void sendNotificationForDelete(Long purchaseId, Long userId) {
        try {
            Assert.notNull(purchaseId, "Purchase ID cannot be null");
            Assert.notNull(userId, "User ID cannot be null");

            log.debug("Sending delete notification for purchase id {}, user id {}", purchaseId, userId);
            sessionService.sendMessage(userId, new DeletePurchaseNotification(purchaseId));
            log.info("Successfully sent delete notification for purchase id {}", purchaseId);
        } catch (Exception ex) {
            String errorMsg = String.format("Failed to send delete notification for purchase %d: %s",
                    purchaseId, ex.getMessage());
            log.error(errorMsg, ex);
            throw new NotificationException(errorMsg, ex);
        }
    }

    public void sendNotificationForCreate(PurchaseDTO purchase, Long userId) {
       try {
           Assert.notNull(purchase, "Purchase cannot be null");
           Assert.notNull(purchase.getId(), "Purchase ID cannot be null");
           Assert.notNull(userId, "User ID cannot be null");
           log.debug("Sending create notification for purchase id {}, user id {}", purchase.getId(), userId);

           sessionService.sendMessage(userId, new CreatePurchaseNotification(purchase));
           log.info("Successfully sent create notification for purchase id {}", purchase.getId());
       } catch (Exception ex) {
           String errorMsg = String.format("Failed to send create notification for purchase %d: %s",
                   purchase != null ? purchase.getId() : null, ex.getMessage());
           log.error(errorMsg, ex);
           throw new NotificationException(errorMsg, ex);
       }
    }

    public void sendNotificationForUpdate(PurchaseDTO purchase, Long userId) {
        try {
            Assert.notNull(purchase, "Purchase cannot be null");
            Assert.notNull(purchase.getId(), "Purchase ID cannot be null");
            Assert.notNull(userId, "User ID cannot be null");

            log.debug("Sending update notification for purchase id {}, user id {}", purchase.getId(), userId);
            sessionService.sendMessage(userId, new UpdatePurchaseNotification(purchase));
            log.info("Successfully sent update notification for purchase id {}", purchase.getId());
        } catch (Exception ex) {
            String errorMsg = String.format("Failed to send update notification for purchase %d: %s",
                    purchase != null ? purchase.getId() : null, ex.getMessage());
            log.error(errorMsg, ex);
            throw new NotificationException(errorMsg, ex);
        }
    }
}