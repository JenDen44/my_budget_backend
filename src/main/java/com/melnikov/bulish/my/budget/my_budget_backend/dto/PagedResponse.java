package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "PagedResponse Model Information")
public class PagedResponse<T> {

    @Schema(description = "Content", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> content;

    @Schema(description = "Page Number", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageNumber;

    @Schema(description = "Page Size", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageSize;

    @Schema(description = "Total elements", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    private long totalElements;

    @Schema(description = "Total pages", example = "22", requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalPages;
}
