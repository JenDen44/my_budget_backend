package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PagedResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseDto;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepo;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PurchaseNotificationService notificationService;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private User testUser;

    private Purchase testPurchase;

    private PurchaseRequest testRequest;

    @BeforeEach
    void setup() {
        testUser = User.builder()
                .id(1)
                .username("testuser")
                .build();

        testPurchase = Purchase.builder()
                .id(1)
                .purchaseDate(LocalDate.now())
                .cost(10.00)
                .quantity(2)
                .category(Category.CLOTHE)
                .totalCost(20.00)
                .user(testUser)
                .build();

        testRequest = PurchaseRequest.builder()
                .purchaseDate(LocalDate.now())
                .cost(10.0)
                .quantity(2)
                .category(Category.CLOTHE)
                .build();
    }

    @Test
    void findPurchaseById() {
        when(purchaseRepo.findById(1)).thenReturn(Optional.of(testPurchase));

        PurchaseDto result = purchaseService.findPurchaseDtoById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(Category.CLOTHE, result.getCategory());
        verify(purchaseRepo, times(1)).findById(1);
    }

    @Test
    void getPurchasesForCurrentUser() {
        Page<Purchase> purchasePage = new PageImpl<>(Collections.singletonList(testPurchase));
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(purchaseRepo.findByUserId(
                anyInt(),
                any(Pageable.class)
        )).thenReturn(purchasePage);

        PagedResponse<PurchaseDto> response = purchaseService.getPurchasesForCurrentUser(
                0, 10, "id", "asc"
        );

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());

        PurchaseDto dto = response.getContent().get(0);
        assertEquals(1, dto.getId());
        assertEquals(Category.CLOTHE, dto.getCategory());

        verify(purchaseRepo).findByUserId(
                testUser.getId(),
                PageRequest.of(0, 10, Sort.by("id").ascending())
        );
    }

    @Test
    void savePurchase() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(purchaseRepo.save(any(Purchase.class))).thenReturn(testPurchase);

        PurchaseDto result = purchaseService.savePurchase(testRequest);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(Category.CLOTHE, result.getCategory());
        assertEquals(20.0, result.getTotalCost());

        verify(purchaseRepo).save(any(Purchase.class));
        verify(notificationService).sendNotificationForCreate(any(PurchaseDto.class), eq(testUser.getId()));
    }

    @Test
    void updatePurchase() {
        PurchaseDto updateDto = new PurchaseDto();
        updateDto.setPurchaseDate(LocalDate.now());
        updateDto.setCost(15.00);
        updateDto.setQuantity(3);
        updateDto.setCategory(Category.EDUCATION);

        when(purchaseRepo.findById(1)).thenReturn(Optional.of(testPurchase));
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(purchaseRepo.save(any(Purchase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseDto result = purchaseService.updatePurchase(updateDto, 1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(Category.EDUCATION, result.getCategory());
        assertEquals(20.0, result.getTotalCost());

        verify(purchaseRepo).findById(1);
        verify(purchaseRepo).save(any(Purchase.class));
        verify(notificationService).sendNotificationForUpdate(any(PurchaseDto.class), eq(testUser.getId()));
    }

    @Test
    void deletePurchase() {
        when(purchaseRepo.findById(1)).thenReturn(Optional.of(testPurchase));
        when(userService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(purchaseRepo).deleteById(1);

        purchaseService.deletePurchase(1);

        verify(purchaseRepo).findById(1);
        verify(purchaseRepo).deleteById(1);
        verify(notificationService).sendNotificationForDelete(1, testUser.getId());
    }
}