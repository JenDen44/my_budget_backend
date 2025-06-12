package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.ResourceNotFoundException;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PagedResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepo;
    private final UserService userService;
    private final PurchaseNotificationService notificationService;

    @Transactional(readOnly = true)
    @Override
    public PurchaseDTO findPurchaseDtoById(Long id) {
        log.info("PurchaseServiceImpl.findPurchaseDtoById() started");

        Purchase purchase = purchaseRepo.findById(id).orElseThrow(() -> {
                    log.error("ResourceNotFoundException {}", id);
                    return new ResourceNotFoundException("Purchase", String.valueOf(id));
                });

        log.info("Found purchase {}", purchase);
        return PurchaseDTO.builder()
                .id(purchase.getId())
                .category(purchase.getCategory())
                .purchaseDate(purchase.getPurchaseDate())
                .cost(purchase.getCost())
                .quantity(purchase.getQuantity())
                .totalCost(purchase.getTotalCost())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResponse<PurchaseDTO> getPurchasesForCurrentUser(
            int pageNo, int pageSize, String sortBy, String sortDir) {
        log.info("PurchaseServiceImpl.getPurchasesForCurrentUser() started");
        log.debug("Parameters: page={}, size={}, sort={}, dir={}", pageNo, pageSize, sortBy, sortDir);

        var currentUser = userService.getCurrentUser();
        log.debug("Current user {}", currentUser);

        var direction = Sort.Direction.fromOptionalString(sortDir).orElse(Sort.Direction.ASC);
        var sort = Sort.by(direction, sortBy);
        var pg = PageRequest.of(pageNo, pageSize, sort);

        var purchasesPage = purchaseRepo.findByUserId(currentUser.getId(), pg);

        var purchaseDtoList = purchasesPage
                .stream()
                .map(PurchaseDTO::new)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                purchaseDtoList,
                purchasesPage.getNumber(),
                purchasesPage.getSize(),
                purchasesPage.getTotalElements(),
                purchasesPage.getTotalPages());
    }

    @Override
    public PurchaseDTO savePurchase(PurchaseRequest purchaseRequest) {
        log.info("PurchaseServiceImpl.savePurchase() is started");

        var currentUser = userService.getCurrentUser();
        log.debug("Current user {}", currentUser);

        var purchase = Purchase.builder()
            .purchaseDate(purchaseRequest.getPurchaseDate())
            .cost(purchaseRequest.getCost())
            .quantity(purchaseRequest.getQuantity())
            .category(purchaseRequest.getCategory())
            .user(currentUser)
            .build();

        var purchaseSavedToDB = purchaseRepo.save(purchase);
        log.debug("Created purchase {}", purchaseSavedToDB);

        var purchaseResponse = new PurchaseDTO(purchaseSavedToDB);

        notificationService.sendNotificationForCreate(purchaseResponse, currentUser.getId());

        return purchaseResponse;
    }

    @Override
    public PurchaseDTO updatePurchase(PurchaseDTO updatedPurchase, Long id) {
        log.info("PurchaseServiceImpl.updatePurchase() is started");

        var currentUser = userService.getCurrentUser();
        log.debug("Current user {}", currentUser);

        var purchaseFromDB = findPurchaseById(id);
        log.debug("Purchase before update {}", purchaseFromDB);

        if (!purchaseFromDB.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't own this purchase");
        }

        boolean changed = false;
        if (!updatedPurchase.getPurchaseDate().equals(purchaseFromDB.getPurchaseDate())) {
            purchaseFromDB.setPurchaseDate(updatedPurchase.getPurchaseDate());
            changed = true;
        }
        if (updatedPurchase.getCost().equals(purchaseFromDB.getCost())) {
            purchaseFromDB.setCost(updatedPurchase.getCost());
            changed = true;
        }
        if (!updatedPurchase.getQuantity().equals(purchaseFromDB.getQuantity())) {
            purchaseFromDB.setQuantity(updatedPurchase.getQuantity());
            changed = true;
        }
        if (!updatedPurchase.getCategory().equals(purchaseFromDB.getCategory())) {
            purchaseFromDB.setCategory(updatedPurchase.getCategory());
            changed = true;
        }
        var purchaseDto = new PurchaseDTO(purchaseFromDB);

        if (!changed) {
            log.warn("No changes detected for purchase ID: {}", id);
            return purchaseDto;
        }
        log.debug("After update {} ", purchaseFromDB);
        purchaseRepo.save(purchaseFromDB);

        notificationService.sendNotificationForUpdate(purchaseDto, currentUser.getId());

        return purchaseDto;
    }

    @Override
    public void deletePurchase(Long id) {
        log.info("PurchaseServiceImpl.deletePurchase() is started");
        log.debug("Purchase to be deleted {} ", id);

        var currentUser = userService.getCurrentUser();
        log.debug("Current user {} ", currentUser);

        Purchase purchase = findPurchaseById(id);

        if (!purchase.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't own this purchase");
        }

        notificationService.sendNotificationForDelete(id, currentUser.getId());
        purchaseRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Purchase findPurchaseById(Long id) {
        return purchaseRepo.findById(id).orElseThrow(() -> {
            log.error("ResourceNotFoundException {}", id);
            return new ResourceNotFoundException("Purchase", String.valueOf(id));
        });
    }
}