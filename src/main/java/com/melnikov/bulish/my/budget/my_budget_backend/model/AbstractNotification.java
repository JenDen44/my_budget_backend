package com.melnikov.bulish.my.budget.my_budget_backend.model;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbstractNotification<Entity> {

    private String type;
    private NotificationStatus status;
    private Entity entity;
}