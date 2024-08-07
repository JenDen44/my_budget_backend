package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

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
    public List<PurchaseDto> getAllPurchases() {

        return purchaseService.findAllPurchases();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDto> getPurchase(@PathVariable Integer id) {
        PurchaseDto purchaseDto = purchaseService.findPurchaseById(id);

            return ResponseEntity.ok(purchaseDto);
    }

    @PostMapping
    public ResponseEntity<PurchaseDto> createPurchase(@RequestBody PurchaseDto purchaseDto) {
        System.out.println("createPurchase method is called");

           return ResponseEntity.ok(purchaseService.savePurchase(purchaseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDto> updatePurchase(@RequestBody PurchaseDto purchaseDto,
                                                @PathVariable Integer id) {
          return ResponseEntity.ok(purchaseService.updatePurchase(purchaseDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePurchase(@PathVariable Integer id) {
        purchaseService.deletePurchase(id);

          return new ResponseEntity<>(HttpStatus.OK);
    }
}