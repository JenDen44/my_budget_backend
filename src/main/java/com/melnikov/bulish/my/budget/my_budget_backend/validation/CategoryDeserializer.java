package com.melnikov.bulish.my.budget.my_budget_backend.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CategoryDeserializer extends JsonDeserializer<Category> {

    private static final Map<String, Category> AVAILABLE_CATEGORIES = Stream.of(Category.values())
            .collect(Collectors.toMap(
                    c -> c.getDisplayName().toLowerCase(),
                    Function.identity()
            ));

    @Override
    public Category deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText().toLowerCase();

        Category category = AVAILABLE_CATEGORIES.get(value);

        if (category == null) throw new InvalidFormatException(
                jsonParser,
                "Invalid category value. Accepted: " + AVAILABLE_CATEGORIES.values(),
                value,
                Category.class
        );

        return category;
    }
}