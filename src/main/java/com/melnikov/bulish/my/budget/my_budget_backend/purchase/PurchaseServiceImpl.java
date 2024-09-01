package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepo;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepo, PurchaseMapper purchaseMapper) {
        this.purchaseRepo = purchaseRepo;
    }

    @Override
    public PurchaseDto findPurchaseById(Integer id) {
        Purchase purchase = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

            return new PurchaseDto(purchase);
    }

    @Override
    public List<PurchaseDto> findAllPurchases() {
        List<Purchase> purchases =  purchaseRepo.findAll();
        List<PurchaseDto> purchaseDtoList = purchases.stream().map(p ->
                new PurchaseDto(p)).toList();

            return purchaseDtoList;
    }


    @Override
    public PurchaseDto savePurchase(PurchaseDto purchase) {
        Purchase purchaseSavedToDB = purchaseRepo.save(new Purchase(purchase));

            return new PurchaseDto(purchaseSavedToDB);
    }

    @Override
    public PurchaseDto updatePurchase(PurchaseDto purchase, Integer id) {
        Purchase purchaseFromDB = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

        purchaseFromDB.setPurchaseDate(purchase.getPurchaseDate());
        purchaseFromDB.setCost(purchase.getCost());
        purchaseFromDB.setCategory(purchase.getCategory());
        purchaseFromDB.setQuantity(purchase.getQuantity());

            return new PurchaseDto(purchaseRepo.save(purchaseFromDB));
    }

    @Override
    public void deletePurchase(Integer id) {
        Purchase purchaseFromDB = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

        purchaseRepo.deleteById(id);

    }
}