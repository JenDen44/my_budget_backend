package com.melnikov.bulish.my.budget.my_budget_backend.converter;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class CategoryConverter implements AttributeConverter<Category, String> {

    private static final Map<String, Category> DISPLAY_VALUE_MAP = Stream.of(Category.values())
            .collect(Collectors.toMap(
                    c -> c.getDisplayName().toLowerCase(),
                    Function.identity()
            ));

    @Override
    public String convertToDatabaseColumn(Category category) {
        return (category != null) ? category.getDisplayName() : null;
    }

    @Override
    public Category convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;

        String normalized = dbValue.trim().toLowerCase();
        Category category = DISPLAY_VALUE_MAP.get(normalized);

        if (category == null) {
            throw new IllegalArgumentException("Invalid category value: " + dbValue);
        }
        return category;
    }
}
