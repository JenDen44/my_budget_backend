package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PurchaseMapper {
    private final ModelMapper mapper;
    @Autowired
    public PurchaseMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Purchase toEntity(PurchaseDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Purchase.class);
    }

    public PurchaseDto toDto(Purchase entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, PurchaseDto.class);
    }
}