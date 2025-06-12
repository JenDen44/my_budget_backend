package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PagedResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
                .username("testuser")
                .build();
        testUser.setId(1L);

        testPurchase = Purchase.builder()
                .purchaseDate(LocalDate.now())
                .cost(BigDecimal.valueOf(10.00))
                .quantity(2)
                .category(Category.CLOTHING)
                .user(testUser)
                .build();
        testPurchase.setId(1L);

        testRequest = PurchaseRequest.builder()
                .purchaseDate(LocalDate.now())
                .cost(BigDecimal.valueOf(10.00))
                .quantity(2)
                .category(Category.CLOTHING)
                .build();
    }

    @Test
    void findPurchaseById() {
        when(purchaseRepo.findById(anyLong())).thenReturn(Optional.of(testPurchase));

        PurchaseDTO result = purchaseService.findPurchaseDtoById(1L);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(Category.CLOTHING, result.getCategory());
        verify(purchaseRepo, times(1)).findById(1L);
    }

    @Test
    void getPurchasesForCurrentUser() {
        Page<Purchase> purchasePage = new PageImpl<>(Collections.singletonList(testPurchase));
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(purchaseRepo.findByUserId(anyLong(), any(Pageable.class))).thenReturn(purchasePage);

        PagedResponse<PurchaseDTO> response = purchaseService.getPurchasesForCurrentUser(
                0, 1, "id", "asc"
        );

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(1, response.getPageSize());
        assertEquals(1, response.getTotalElements());

        PurchaseDTO dto = response.getContent().getFirst();
        assertEquals(1, dto.getId());
        assertEquals(Category.CLOTHING, dto.getCategory());

        verify(purchaseRepo).findByUserId(
                testUser.getId(),
                PageRequest.of(0, 1, Sort.by("id").ascending())
        );
    }

    @Test
    void savePurchase() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(purchaseRepo.save(any(Purchase.class))).thenReturn(testPurchase);

        PurchaseDTO result = purchaseService.savePurchase(testRequest);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(Category.CLOTHING, result.getCategory());
        assertEquals(20.00, result.getTotalCost().doubleValue());

        verify(purchaseRepo).save(any(Purchase.class));
        verify(notificationService).sendNotificationForCreate(any(PurchaseDTO.class), eq(testUser.getId()));
    }

    @Test
    void updatePurchase() {
        PurchaseDTO updateDto = PurchaseDTO.builder()
                .category(Category.EDUCATION)
                .cost(BigDecimal.valueOf(10.00))
                .purchaseDate(LocalDate.now().minusDays(15))
                .quantity(10)
                .build();

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(purchaseRepo.findById(anyLong())).thenReturn(Optional.of(testPurchase));

        PurchaseDTO result = purchaseService.updatePurchase(updateDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Category.EDUCATION, result.getCategory());
        assertEquals(100.00, result.getTotalCost().doubleValue());

        verify(userService).getCurrentUser();
        verify(purchaseRepo).findById(anyLong());
        verify(purchaseRepo).save(any(Purchase.class));
        verify(notificationService).sendNotificationForUpdate(any(PurchaseDTO.class), eq(testUser.getId()));
    }

    @Test
    void deletePurchase() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(purchaseRepo.findById(anyLong())).thenReturn(Optional.of(testPurchase));
        doNothing().when(purchaseRepo).deleteById(anyLong());

        purchaseService.deletePurchase(1L);

        verify(purchaseRepo).findById(anyLong());
        verify(purchaseRepo).deleteById(anyLong());
        verify(notificationService).sendNotificationForDelete(anyLong(), anyLong());
    }
}
