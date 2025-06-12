package com.melnikov.bulish.my.budget.my_budget_backend.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class AbstractEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected Long id;
}