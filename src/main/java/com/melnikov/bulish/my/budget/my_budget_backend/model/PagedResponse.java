package com.melnikov.bulish.my.budget.my_budget_backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(
        name = "PagedResponse",
        description = "Schema to send content with pagination info"
)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedResponse<T> {

    @Schema(
            description = "Collection with requested entities"
    )
    private List<T> content;

    @Schema(
            description = "Page number"
    )
    private int pageNumber;

    @Schema(
            description = "Page size"
    )
    private int pageSize;

    @Schema(
            description = "Total elements"
    )
    private long totalElements;

    @Schema(
            description = "Total pages"
    )
    private int totalPages;
}
