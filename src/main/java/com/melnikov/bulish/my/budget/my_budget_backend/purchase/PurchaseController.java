package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.melnikov.bulish.my.budget.my_budget_backend.utils.AppConstantsConfig;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseServiceImpl purchaseService;

    @Autowired
    public PurchaseController(PurchaseServiceImpl purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public List<PurchaseDto> getPurchasePage(@RequestParam(value = "pageNo", defaultValue = AppConstantsConfig.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                             @RequestParam(value = "pageSize", defaultValue = AppConstantsConfig.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                             @RequestParam(value = "sortBy", defaultValue = AppConstantsConfig.DEFAULT_SORT_BY, required = false) String sortBy,
                                             @RequestParam(value = "sortDir", defaultValue = AppConstantsConfig.DEFAULT_SORT_DIR, required = false) String sortDir) {

             return purchaseService.getPurchasesPage(pageNo,pageSize,sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDto> getPurchase(@PathVariable Integer id) {
        PurchaseDto purchaseDto = purchaseService.findPurchaseById(id);

            return ResponseEntity.ok(purchaseDto);
    }

    @PostMapping
    public ResponseEntity<PurchaseDto> createPurchase(@Valid @RequestBody PurchaseDto purchaseDto) {
        System.out.println("createPurchase method is called");

           return ResponseEntity.ok(purchaseService.savePurchase(purchaseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDto> updatePurchase(@Valid @RequestBody PurchaseDto purchaseDto,
                                                @PathVariable Integer id) {
          return ResponseEntity.ok(purchaseService.updatePurchase(purchaseDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePurchase(@PathVariable Integer id) {
        purchaseService.deletePurchase(id);

          return new ResponseEntity<>(HttpStatus.OK);
    }
}