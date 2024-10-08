package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.melnikov.bulish.my.budget.my_budget_backend.utils.AppConstantsConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@Tag(name = "Purchases")
public class PurchaseController {

    private final PurchaseServiceImpl purchaseService;

    @Autowired
    public PurchaseController(PurchaseServiceImpl purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Operation(
        description = "Endpoint for get all purchases",
        summary = "If you need to get all purchases for the current user, please use this endpoint",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Unauthorized/Invalid token",
                responseCode = "401"
            ),
            @ApiResponse(
                description = "Validation error",
                responseCode = "422"
            )
        }
    )
    @GetMapping
    public List<PurchaseDto> getPurchasePage(
        @RequestParam(value = "pageNo", defaultValue = AppConstantsConfig.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = AppConstantsConfig.DEFAULT_PAGE_SIZE, required = false) int pageSize,
        @RequestParam(value = "sortBy", defaultValue = AppConstantsConfig.DEFAULT_SORT_BY, required = false) String sortBy,
        @RequestParam(value = "sortDir", defaultValue = AppConstantsConfig.DEFAULT_SORT_DIR, required = false) String sortDir
    ) {
        return purchaseService.getPurchasesForCurrentUser(pageNo, pageSize, sortBy, sortDir);
    }

    @Operation(
        description = "Endpoint for get purchase by id",
        summary = "If you need to get a purchase by id for the current user, please use this endpoint",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Unauthorized/Invalid token",
                responseCode = "401"
            ),
            @ApiResponse(
                description = "Not Found",
                responseCode = "404"
            ),
            @ApiResponse(
                description = "Validation error",
                responseCode = "422"
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDto> getPurchase(@PathVariable Integer id) {
        return ResponseEntity.ok(purchaseService.findPurchaseById(id));
    }

    @Operation(
        description = "Endpoint for purchase creation",
        summary = "If you need to create a new purchase for the current user, please use this endpoint",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Unauthorized/Invalid token",
                responseCode = "401"
            ),
            @ApiResponse(
                description = "Validation error",
                responseCode = "422"
            )
        }
    )
    @PostMapping
    public ResponseEntity<PurchaseDto> createPurchase(@Valid @RequestBody PurchaseRequest purchaseRequest) {
        return ResponseEntity.ok(purchaseService.savePurchase(purchaseRequest));
    }

    @Operation(
        description = "Endpoint for update purchase by id",
        summary = "If you need to update a purchase by id for the current user, please use this endpoint",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Unauthorized/Invalid token",
                responseCode = "401"
            ),
            @ApiResponse(
                description = "Not Found",
                responseCode = "404"
            ),
            @ApiResponse(
                description = "Validation error",
                responseCode = "422"
            )
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDto> updatePurchase(
        @Valid @RequestBody PurchaseDto request,
        @PathVariable Integer id
    ) {
        return ResponseEntity.ok(purchaseService.updatePurchase(request, id));
    }

    @Operation(
        description = "Endpoint for delete purchase by id",
        summary = "If you need to delete a purchase by id for the current user, please use this endpoint",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Unauthorized/Invalid token",
                responseCode = "401"
            ),
            @ApiResponse(
                description = "Not Found",
                responseCode = "404"
            ),
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePurchase(@PathVariable Integer id) {
        purchaseService.deletePurchase(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}