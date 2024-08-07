package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepo;
    private final PurchaseMapper purchaseMapper;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepo, PurchaseMapper purchaseMapper) {
        this.purchaseRepo = purchaseRepo;
        this.purchaseMapper = purchaseMapper;
    }

    @Override
    public PurchaseDto findPurchaseById(Integer id) {
        Purchase purchase = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

            return purchaseMapper.toDto(purchase);
    }

    @Override
    public List<PurchaseDto> findAllPurchases() {
        List<Purchase> purchases = (List<Purchase>) purchaseRepo.findAll();
        List<PurchaseDto> purchaseDtoList = purchases.stream().map(purchase -> purchaseMapper.toDto(purchase)).toList();

            return purchaseDtoList;
    }


    @Override
    public PurchaseDto savePurchase(PurchaseDto purchase) {
        Purchase purchaseToDB = purchaseMapper.toEntity(purchase);
        Purchase purchaseSavedToDB = purchaseRepo.save(purchaseToDB);

            return purchaseMapper.toDto(purchaseSavedToDB);
    }

    @Override
    public PurchaseDto updatePurchase(PurchaseDto purchase, Integer id) {
        Purchase purchaseFromDB = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

        Purchase updatedPurchase = purchaseRepo.save(purchaseMapper.toEntity(purchase));

            return purchaseMapper.toDto(updatedPurchase);
    }

    @Override
    public void deletePurchase(Integer id) {
        Purchase purchaseFromDB = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

        purchaseRepo.deleteById(id);

    }
}