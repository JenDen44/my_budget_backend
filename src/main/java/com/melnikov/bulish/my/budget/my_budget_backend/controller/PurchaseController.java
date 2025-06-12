package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.melnikov.bulish.my.budget.my_budget_backend.constants.PaginationConstants;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PagedResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchases")
@Tag(name = "Purchases", description = "Manage user purchase records")
@Validated
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(
            summary = "List purchases",
            description = "Get paginated purchases for current user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page with purchases constructed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PagedResponse.class),
                                    examples = @ExampleObject(
                                            name = "PagedResponseExample",
                                            summary = "Pagination example",
                                            value = """
                                                    {
                                                      "content": [
                                                        {
                                                          "id": 1,
                                                          "Category": "Clothing",
                                                          "cost": 25.00,
                                                          "quantity": 2,
                                                          "purchaseDate" : "2025-04-04"
                                                        },
                                                        {
                                                          "id": 2,
                                                          "Category": "Food",
                                                          "cost": 1500.00,
                                                          "quantity": 10,
                                                          "purchaseDate" : "2025-06-04"
                                                        }
                                                      ],
                                                      "pageNumber": 0,
                                                      "pageSize": 20,
                                                      "totalElements": 2,
                                                      "totalPages": 1
                                                    }
                                                    """
                                    )

                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid or expired token",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 401, \"message\":\"Invalid or expired token\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 422, \"message\":\"Validation Error: page size can't be negative or zero\", \"fields\": \"null\"}"
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public PagedResponse<PurchaseDTO> getPurchasePage(
        @RequestParam(value = "pageNo", defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER, required = false)
        @Min(value = 0, message = "page number can't be negative" ) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE, required = false)
        @Min(value = 1, message = "page size can't be negative or zero")
        @Max(value = 100, message = "page size can't be more 100") int pageSize,
        @RequestParam(value = "sortBy", defaultValue = PaginationConstants.DEFAULT_SORT_BY, required = false) String sortBy,
        @RequestParam(value = "sortDir", defaultValue = PaginationConstants.DEFAULT_SORT_DIR, required = false) String sortDir
    ) {
        return purchaseService.getPurchasesForCurrentUser(pageNo, pageSize, sortBy, sortDir);
    }

    @Operation(
            summary = "Get purchase by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Purchase is found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PurchaseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{\"id\":1,\"category\":\"FOOD\", \"cost\": 1500.00," +
                                                    " \"quantity\": 2,\"purchaseDate\" : \"2025-06-04\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 404, \"message\":\"product not found\", \"fields\": \"null\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 401, \"message\":\"Invalid or expired token\"}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public PurchaseDTO getPurchase(@PathVariable Long id) {
        return purchaseService.findPurchaseDtoById(id);
    }

    @Operation(
            summary = "Create purchase",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Purchase created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PurchaseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{\"id\":1,\"category\":\"FOOD\", \"cost\": 1500.00," +
                                                    " \"quantity\": 2,\"purchaseDate\" : \"2025-06-04\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "ValidationErrorExample",
                                            value = """
                                                    {
                                                      "code": 422,
                                                      "message": "Validation Error",
                                                      "fields": [
                                                        {
                                                          "field": "category",
                                                          "message": "category is required"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 401, \"message\":\"Invalid or expired token\"}"
                                    )
                            )
                    )
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PurchaseDTO createPurchase(@RequestBody @Valid PurchaseRequest purchaseRequest) {
        return purchaseService.savePurchase(purchaseRequest);
    }

    @Operation(
            summary = "Update purchase",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Purchase updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PurchaseDTO.class),
                                    examples = @ExampleObject(
                                            value = "{\"id\":1,\"category\":\"FOOD\", \"cost\": 1500.00," +
                                                    " \"quantity\": 2,\"purchaseDate\" : \"2025-06-04\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "ValidationErrorExample",
                                            value = """
                                                    {
                                                      "code": 422,
                                                      "message": "Validation Error",
                                                      "fields": [
                                                        {
                                                          "field": "category",
                                                          "message": "category is required"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 404, \"message\":\"product not found\", \"fields\": \"null\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 401, \"message\":\"Invalid or expired token\"}"
                                    )
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    public PurchaseDTO updatePurchase(@RequestBody @Valid PurchaseDTO request, @PathVariable Long id) {
        return purchaseService.updatePurchase(request, id);
    }

    @Operation(
            summary = "Delete purchase",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Deleted successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 204}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 404, \"message\":\"product not found\", \"fields\": \"null\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 401, \"message\":\"Invalid or expired token\"}"
                                    )
                            )
                    )
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
    }
}