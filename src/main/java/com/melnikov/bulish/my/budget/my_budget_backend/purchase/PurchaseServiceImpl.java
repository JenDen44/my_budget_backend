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

import java.time.LocalDate;
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

    public List<PurchaseDto> getPurchasesForCurrentUser(int pageNo, int pageSize, String sortBy, String sortDir) {

        User currentUser = userService.getCurrentUser();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pg = PageRequest.of(pageNo , pageSize , sort);
         Page<Purchase> purchasesByCurrentUser = purchaseRepo.findByUserWithPagination(currentUser.getId(), pg);

        if (purchasesByCurrentUser.isEmpty()) throw new PurchaseNotFoundException("No one purchases was found in DB");

        List<PurchaseDto> purchases = purchasesByCurrentUser
                .getContent().stream().map(p -> new PurchaseDto(p)).collect(Collectors.toList());

            return purchases;
    }

    @Override
    public PurchaseDto savePurchase(PurchaseDto purchaseDto) {
        User currentUser = userService.getCurrentUser();
        Purchase purchase = new Purchase(purchaseDto);
        LocalDate currentDate = LocalDate.now();
        purchase.setPurchaseDate(currentDate);
        purchase.setUser(currentUser);
        Purchase purchaseSavedToDB = purchaseRepo.save(purchase);

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