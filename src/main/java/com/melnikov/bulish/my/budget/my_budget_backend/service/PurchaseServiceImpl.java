package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.ResourceNotFoundException;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseDto;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepo;
    private final UserServiceImpl userService;
    private final PurchaseNotificationService notificationService;

    @Override
    public PurchaseDto findPurchaseDtoById(Integer id) {
        Purchase purchase = purchaseRepo.findById(id).orElseThrow(() -> {
                    log.error("ResourceNotFoundException {}", id);
                    return new ResourceNotFoundException("Purchase", String.valueOf(id));
                });

        return new PurchaseDto(purchase);
    }

    public List<PurchaseDto> getPurchasesForCurrentUser(int pageNo, int pageSize, String sortBy, String sortDir) {
        log.info("PurchaseServiceImpl.getPurchasesForCurrentUser() pageNo {}, pageSize {}, sortBy {}, sortDir {}",
            pageNo, pageSize, sortBy, sortDir);

        var currentUser = userService.getCurrentUser();
        log.debug("Current user id {}, username {}", currentUser.getId(), currentUser.getUsername());

        var sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
            Sort.by(sortBy).ascending() :
            Sort.by(sortBy).descending();
        var pg = PageRequest.of(pageNo, pageSize, sort);
        var purchasesByCurrentUser = purchaseRepo.findByUserId(currentUser.getId(), pg);

        log.info("purchases list from DB is empty ? {} ", purchasesByCurrentUser.isEmpty());
        log.debug("{}", purchasesByCurrentUser.getContent());

        return purchasesByCurrentUser
            .getContent()
            .stream()
            .map(PurchaseDto::new)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseDto savePurchase(PurchaseRequest purchaseRequest) {
        log.info("PurchaseServiceImpl.savePurchase() is started");

        var currentUser = userService.getCurrentUser();
        log.debug("Current user id {}, username {}", currentUser.getId(), currentUser.getUsername());

        var purchase = Purchase.builder()
            .purchaseDate(purchaseRequest.getPurchaseDate())
            .cost(purchaseRequest.getCost())
            .quantity(purchaseRequest.getQuantity())
            .category(purchaseRequest.getCategory())
            .totalCost(purchaseRequest.getCost() * purchaseRequest.getQuantity())
                .user(currentUser)
            .build();

        var purchaseSavedToDB = purchaseRepo.save(purchase);
        log.debug("created purchase id {} and category {}", purchaseSavedToDB.getId(), purchaseSavedToDB.getCategory());
        var purchaseDto = new PurchaseDto(purchaseSavedToDB);

        notificationService.sendNotificationForCreate(purchaseDto, currentUser.getId());

        return purchaseDto;
    }

    @Override
    @Transactional
    public PurchaseDto updatePurchase(PurchaseDto purchase, Integer id) {
        log.info("PurchaseServiceImpl.updatePurchase() is started");
        log.info("purchase id {} ", id);

        var currentUser = userService.getCurrentUser();
        var purchaseFromDB = findPurchaseById(id);

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
    @Transactional
    public void deletePurchase(Integer id) {
        log.info("PurchaseServiceImpl.deletePurchase() is started");
        log.debug("Purchase to be deleted {} ", id);

        var currentUser = userService.getCurrentUser();
        findPurchaseById(id);

        notificationService.sendNotificationForDelete(id, currentUser.getId());
        purchaseRepo.deleteById(id);
    }

    public Purchase findPurchaseById(Integer id) {
        Purchase purchase = purchaseRepo.findById(id).orElseThrow(() -> {
            log.error("ResourceNotFoundException {}", id);
            return new ResourceNotFoundException("Purchase", String.valueOf(id));
        });

        return purchase;
    }
}