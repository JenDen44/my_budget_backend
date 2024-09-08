package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepo;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepo) {
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
        List<Purchase> purchases = (List<Purchase>) purchaseRepo.findAll();
        List<PurchaseDto> purchaseDtoList = purchases.stream().map(p -> new PurchaseDto(p)).toList();

        if (purchaseDtoList.isEmpty()) throw new PurchaseNotFoundException("No one purchase was found in DB");

            return purchaseDtoList;
    }

    public List<PurchaseDto> getPurchasesPage(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pg = PageRequest.of(pageNo , pageSize , sort);
        Page<Purchase> pagePurchases = purchaseRepo.findAll(pg);
        if (pagePurchases.isEmpty()) throw new PurchaseNotFoundException("No one purchases was found in DB");

        List<PurchaseDto> purchases = pagePurchases
                .getContent().stream().map(p -> new PurchaseDto(p)).collect(Collectors.toList());

            return purchases;
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