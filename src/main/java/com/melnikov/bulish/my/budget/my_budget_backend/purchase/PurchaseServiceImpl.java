package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.melnikov.bulish.my.budget.my_budget_backend.notification.*;
import com.melnikov.bulish.my.budget.my_budget_backend.user.User;
import com.melnikov.bulish.my.budget.my_budget_backend.user.UserServiceImpl;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepo;
    private final UserServiceImpl userService;
    private final PurchaseNotificationService notificationService;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepo, UserServiceImpl userService, PurchaseNotificationService notificationService) {
        this.purchaseRepo = purchaseRepo;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public PurchaseDto findPurchaseById(Integer id) {
        Purchase purchase = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

            return new PurchaseDto(purchase);
    }

    @Override
    public List<PurchaseDto> findAllPurchases() {
        List<PurchaseDto> purchases = new ArrayList<>();
        List<Purchase> purchasesFromDB = (List<Purchase>) purchaseRepo.findAll();

        log.debug("PurchaseServiceImpl.findAllPurchases() purchases from DB is empty ? {} ", purchasesFromDB.isEmpty());
        log.debug("{}", purchasesFromDB);

        purchases = purchasesFromDB.stream().map(p -> new PurchaseDto(p)).toList();

            return purchases;
    }

    public List<PurchaseDto> getPurchasesForCurrentUser(int pageNo, int pageSize, String sortBy, String sortDir) {
        log.debug("PurchaseServiceImpl.getPurchasesForCurrentUser() pageNo {}, pageSize {}, sortBy {}, " +
                        "sortDir {} ", pageNo, pageSize, sortBy, sortDir);

        List<PurchaseDto> purchases = new ArrayList<>();

        User currentUser = userService.getCurrentUser();
        log.debug("Current user {} ", currentUser);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pg = PageRequest.of(pageNo , pageSize , sort);
        Page<Purchase> purchasesByCurrentUser = purchaseRepo.findByUserWithPagination(currentUser.getId(), pg);

        log.debug("purchases list from DB is empty ? {} ", purchasesByCurrentUser.isEmpty());
        log.debug("{}", purchasesByCurrentUser.getContent());

        purchases = purchasesByCurrentUser
                .getContent().stream().map(p -> new PurchaseDto(p)).collect(Collectors.toList());

            return purchases;
    }

    @Override
    public PurchaseDto savePurchase(PurchaseRequest purchaseRequest) {
        log.debug("PurchaseServiceImpl.savePurchase() is started");

        User currentUser = userService.getCurrentUser();
        log.debug("current user {} ", currentUser);

        var purchase = Purchase.builder()
                        .purchaseDate(purchaseRequest.getPurchaseDate())
                        .cost(purchaseRequest.getCost())
                        .quantity(purchaseRequest.getQuantity())
                        .category(purchaseRequest.getCategory())
                        .totalCost(purchaseRequest.getCost() * purchaseRequest.getQuantity())
                        .build();

        purchase.setUser(currentUser);
        Purchase purchaseSavedToDB = purchaseRepo.save(purchase);
        log.debug("created purchase {} ", purchaseSavedToDB);
        PurchaseDto purchaseDto = new PurchaseDto(purchaseSavedToDB);
        notificationService.sendNotificationForCreate(purchaseDto,currentUser.getId());


            return purchaseDto;
    }

    @Override
    public PurchaseDto updatePurchase(PurchaseDto purchase, Integer id) {
        log.debug("PurchaseServiceImpl.updatePurchase() is started");
        log.debug("request {}, id {} ", purchase, id);
        User currentUser = userService.getCurrentUser();

        Purchase purchaseFromDB = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        log.debug("before update {} ", purchaseFromDB);

        purchaseFromDB.setPurchaseDate(purchase.getPurchaseDate());
        purchaseFromDB.setCost(purchase.getCost());
        purchaseFromDB.setCategory(purchase.getCategory());
        purchaseFromDB.setQuantity(purchase.getQuantity());
        log.debug("after update {} ", purchaseFromDB);
        purchaseRepo.save(purchaseFromDB);
        PurchaseDto purchaseDto = new PurchaseDto(purchaseFromDB);
        notificationService.sendNotificationForUpdate(purchaseDto, currentUser.getId());

            return purchaseDto;
    }

    @Override
    public void deletePurchase(Integer id) {
        log.debug("PurchaseServiceImpl.deletePurchase() is started");
        log.debug("Purchase to be deleted {} ", id);
        User currentUser = userService.getCurrentUser();

        Purchase purchaseFromDB = purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        notificationService.sendNotificationForDelete(id, currentUser.getId());

        purchaseRepo.deleteById(id);
    }
}