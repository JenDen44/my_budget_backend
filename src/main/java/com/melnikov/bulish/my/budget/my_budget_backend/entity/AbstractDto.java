package com.melnikov.bulish.my.budget.my_budget_backend.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractDto implements Serializable {

    protected Integer id;
}

