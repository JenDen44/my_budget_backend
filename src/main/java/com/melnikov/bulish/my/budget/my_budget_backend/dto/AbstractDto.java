package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Schema(description = "Base DTO with identifier")
public class AbstractDto implements Serializable {
    @Schema(description = "Unique identifier", example = "123")
    protected Long id;
}

