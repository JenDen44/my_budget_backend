package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.melnikov.bulish.my.budget.my_budget_backend.notification.*;
import com.melnikov.bulish.my.budget.my_budget_backend.user.UserServiceImpl;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    public PurchaseServiceImpl(
        PurchaseRepository purchaseRepo,
        UserServiceImpl userService,
        PurchaseNotificationService notificationService
    ) {
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

    public List<PurchaseDto> getPurchasesForCurrentUser(int pageNo, int pageSize, String sortBy, String sortDir) {
        log.debug(
            "PurchaseServiceImpl.getPurchasesForCurrentUser() pageNo {}, pageSize {}, sortBy {}, sortDir {}",
            pageNo,
            pageSize,
            sortBy,
            sortDir
        );

        var currentUser = userService.getCurrentUser();

        log.debug("Current user {} ", currentUser);

        var sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
            Sort.by(sortBy).ascending() :
            Sort.by(sortBy).descending();
        var pg = PageRequest.of(pageNo, pageSize, sort);
        var purchasesByCurrentUser = purchaseRepo.findByUserWithPagination(currentUser.getId(), pg);

        log.debug("purchases list from DB is empty ? {} ", purchasesByCurrentUser.isEmpty());
        log.debug("{}", purchasesByCurrentUser.getContent());

        return purchasesByCurrentUser
            .getContent()
            .stream()
            .map(p -> new PurchaseDto(p))
            .collect(Collectors.toList());
    }

    @Override
    public PurchaseDto savePurchase(PurchaseRequest purchaseRequest) {
        log.debug("PurchaseServiceImpl.savePurchase() is started");

        var currentUser = userService.getCurrentUser();

        log.debug("current user {} ", currentUser);

        var purchase = Purchase.builder()
            .purchaseDate(purchaseRequest.getPurchaseDate())
            .cost(purchaseRequest.getCost())
            .quantity(purchaseRequest.getQuantity())
            .category(purchaseRequest.getCategory())
            .totalCost(purchaseRequest.getCost() * purchaseRequest.getQuantity())
            .build();

        purchase.setUser(currentUser);

        var purchaseSavedToDB = purchaseRepo.save(purchase);

        log.debug("created purchase {} ", purchaseSavedToDB);

        var purchaseDto = new PurchaseDto(purchaseSavedToDB);

        notificationService.sendNotificationForCreate(purchaseDto, currentUser.getId());

        return purchaseDto;
    }

    @Override
    public PurchaseDto updatePurchase(PurchaseDto purchase, Integer id) {
        log.debug("PurchaseServiceImpl.updatePurchase() is started");
        log.debug("request {}, id {} ", purchase, id);

        var currentUser = userService.getCurrentUser();
        var purchaseFromDB = purchaseRepo.findById(id)
            .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));

        log.debug("before update {} ", purchaseFromDB);

        purchaseFromDB.setPurchaseDate(purchase.getPurchaseDate());
        purchaseFromDB.setCost(purchase.getCost());
        purchaseFromDB.setCategory(purchase.getCategory());
        purchaseFromDB.setQuantity(purchase.getQuantity());

        log.debug("after update {} ", purchaseFromDB);

        purchaseRepo.save(purchaseFromDB);

        var purchaseDto = new PurchaseDto(purchaseFromDB);

        notificationService.sendNotificationForUpdate(purchaseDto, currentUser.getId());

        return purchaseDto;
    }

    @Override
    public void deletePurchase(Integer id) {
        log.debug("PurchaseServiceImpl.deletePurchase() is started");
        log.debug("Purchase to be deleted {} ", id);

        var currentUser = userService.getCurrentUser();

        purchaseRepo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        notificationService.sendNotificationForDelete(id, currentUser.getId());
        purchaseRepo.deleteById(id);
    }
}