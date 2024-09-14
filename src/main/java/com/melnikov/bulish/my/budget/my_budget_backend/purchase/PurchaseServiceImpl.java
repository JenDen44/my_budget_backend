package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.melnikov.bulish.my.budget.my_budget_backend.user.User;
import com.melnikov.bulish.my.budget.my_budget_backend.user.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepo;
    private final UserServiceImpl userService;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepo, UserServiceImpl userService) {
        this.purchaseRepo = purchaseRepo;
        this.userService = userService;
    }

    @Override
    public PurchaseResponse findPurchaseById(Integer id) {
        Purchase purchase = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

            return new PurchaseResponse(purchase);
    }

    @Override
    public List<PurchaseResponse> findAllPurchases() {
        List<PurchaseResponse> purchases = new ArrayList<>();
        List<Purchase> purchasesFromDB = (List<Purchase>) purchaseRepo.findAll();
        purchases = purchasesFromDB.stream().map(p -> new PurchaseResponse(p)).toList();

       /* if (purchases.isEmpty()) throw new PurchaseNotFoundException("No one purchase was found in DB");*/
        //TODO add logging instead of exception

            return purchases;
    }

    public List<PurchaseResponse> getPurchasesForCurrentUser(int pageNo, int pageSize, String sortBy, String sortDir) {
        List<PurchaseResponse> purchases = new ArrayList<>();

        User currentUser = userService.getCurrentUser();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pg = PageRequest.of(pageNo , pageSize , sort);
        Page<Purchase> purchasesByCurrentUser = purchaseRepo.findByUserWithPagination(currentUser.getId(), pg);

        if (purchasesByCurrentUser.isEmpty()) return purchases;
        //TODO добавить логирование вместо ошибки

        purchases = purchasesByCurrentUser
                .getContent().stream().map(p -> new PurchaseResponse(p)).collect(Collectors.toList());

            return purchases;
    }

    @Override
    public PurchaseResponse savePurchase(PurchaseRequest purchaseRequest) {
        User currentUser = userService.getCurrentUser();

        var purchase = Purchase.builder()
                        .purchaseDate(purchaseRequest.getPurchaseDate())
                        .cost(purchaseRequest.getCost())
                        .quantity(purchaseRequest.getQuantity())
                        .category(purchaseRequest.getCategory())
                        .totalCost(purchaseRequest.getCost() * purchaseRequest.getQuantity())
                        .build();

        purchase.setUser(currentUser);
        Purchase purchaseSavedToDB = purchaseRepo.save(purchase);

            return new PurchaseResponse(purchaseSavedToDB);
    }

    @Override
    public PurchaseResponse updatePurchase(PurchaseResponse purchase, Integer id) {
        Purchase purchaseFromDB = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

        purchaseFromDB.setPurchaseDate(purchase.getPurchaseDate());
        purchaseFromDB.setCost(purchase.getCost());
        purchaseFromDB.setCategory(purchase.getCategory());
        purchaseFromDB.setQuantity(purchase.getQuantity());

            return new PurchaseResponse(purchaseRepo.save(purchaseFromDB));
    }

    @Override
    public void deletePurchase(Integer id) {
        Purchase purchaseFromDB = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

        purchaseRepo.deleteById(id);

    }
}