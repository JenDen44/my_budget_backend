package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.model.CreatePurchaseNotification;
import com.melnikov.bulish.my.budget.my_budget_backend.model.DeletePurchaseNotification;
import com.melnikov.bulish.my.budget.my_budget_backend.model.UpdatePurchaseNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseNotificationServiceTest {

    @Mock
    private WebSocketSessionService sessionService;

    @InjectMocks
    private PurchaseNotificationService notificationService;

    private final Long userId = 1L;
    private final PurchaseDTO purchase = PurchaseDTO.builder()
            .id(100L)
            .purchaseDate(LocalDate.now())
            .cost(BigDecimal.valueOf(10.00))
            .quantity(2)
            .totalCost(BigDecimal.valueOf(20.00))
            .category(Category.FOOD)
            .build();

    @Test
    void sendNotificationForCreate() {
        notificationService.sendNotificationForCreate(purchase, userId);

        verify(sessionService).sendMessage(
                eq(userId),
                argThat(notification ->
                        notification instanceof CreatePurchaseNotification &&
                                ((CreatePurchaseNotification) notification).getEntity().equals(purchase)
                )
        );
    }

    @Test
    void sendNotificationForUpdate() {
        notificationService.sendNotificationForUpdate(purchase, userId);

        verify(sessionService).sendMessage(
                eq(userId),
                argThat(notification ->
                        notification instanceof UpdatePurchaseNotification &&
                                ((UpdatePurchaseNotification) notification).getEntity().equals(purchase)
                )
        );
    }

    @Test
    void sendNotificationForDelete() {
        notificationService.sendNotificationForDelete(100L, userId);

        verify(sessionService).sendMessage(
                eq(userId),
                argThat(notification ->
                        notification instanceof DeletePurchaseNotification &&
                                ((DeletePurchaseNotification) notification).getEntity().equals(100L)
                )
        );
    }
}
