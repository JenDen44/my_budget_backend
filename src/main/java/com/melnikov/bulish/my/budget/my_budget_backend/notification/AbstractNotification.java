package com.melnikov.bulish.my.budget.my_budget_backend.notification;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AbstractNotification<T> {

    private String type;
    private NotificationStatus status;
    private T entity;
}