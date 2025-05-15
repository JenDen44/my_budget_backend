package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.ResourceNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepo;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private PurchaseNotificationService notificationService;

    @InjectMocks
    private PurchaseServiceImpl service;

    private User currentUser;


    @BeforeEach
    void setup() {
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setUsername("testuser");
    }

    @Test
    void findPurchaseById() {
        Purchase p = new Purchase();
        p.setUser(currentUser);
        p.setId(10);
        when(purchaseRepo.findById(10)).thenReturn(Optional.of(p));

        PurchaseDto dto = service.findPurchaseDtoById(10);
        assertThat(dto).isNotNull();
        verify(purchaseRepo).findById(10);
    }

    @Test
    void findPurchaseDtoById_404() {
        when(purchaseRepo.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findPurchaseDtoById(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPurchasesForCurrentUserPage() {
        Purchase p1 = new Purchase();
        p1.setId(1);
        p1.setUser(currentUser);
        Purchase p2 = new Purchase();
        p2.setId(2);
        p2.setUser(currentUser);
        List<Purchase> list = Arrays.asList(p1, p2);

        Page<Purchase> page = new PageImpl<>(list);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepo.findByUserId(eq(currentUser.getId()), any(PageRequest.class))).thenReturn(page);

        PagedResponse<PurchaseDto> response = service.getPurchasesForCurrentUser(0, 10, "id", "asc");
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        verify(purchaseRepo).findByUserId(eq(currentUser.getId()), any(PageRequest.class));
    }

    @Test
    void savePurchase() {
        PurchaseRequest req = new PurchaseRequest();
        req.setCost(50.00);
        req.setQuantity(2);
        req.setCategory(null);
        req.setPurchaseDate(LocalDate.now());

        Purchase saved = new Purchase();
        saved.setId(1);
        saved.setCost(req.getCost());
        saved.setQuantity(req.getQuantity());
        saved.setTotalCost(req.getCost() * req.getQuantity());
        saved.setUser(currentUser);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepo.save(any(Purchase.class))).thenReturn(saved);

        PurchaseDto result = service.savePurchase(req);
        assertThat(result).isNotNull();
        verify(purchaseRepo).save(any(Purchase.class));
        verify(notificationService).sendNotificationForCreate(any(PurchaseDto.class), eq(currentUser.getId()));
    }

    @Test
    void updatePurchase() {
        Purchase existing = new Purchase();
        existing.setId(1);
        existing.setCost(10.00);
        existing.setQuantity(1);
        existing.setCategory(null);
        existing.setPurchaseDate(LocalDate.now());
        existing.setUser(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepo.findById(1)).thenReturn(Optional.of(existing));
        when(purchaseRepo.save(any(Purchase.class))).thenAnswer(i -> i.getArguments()[0]);

        PurchaseDto updateDto = new PurchaseDto();
        updateDto.setCost(100.00);
        updateDto.setQuantity(3);
        updateDto.setCategory(null);
        updateDto.setPurchaseDate(LocalDate.now());

        PurchaseDto result = service.updatePurchase(updateDto, 1);
        assertThat(result).isNotNull();
        assertThat(result.getCost()).isEqualTo(100);
        verify(purchaseRepo).save(any(Purchase.class));
        verify(notificationService).sendNotificationForUpdate(any(PurchaseDto.class), eq(currentUser.getId()));
    }

    @Test
    void deletePurchase() {
        Purchase purchase = new Purchase();
        purchase.setId(1);
        purchase.setUser(currentUser);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepo.findById(1)).thenReturn(Optional.of(purchase));

        service.deletePurchase(1);
        verify(purchaseRepo).deleteById(1);
        verify(notificationService).sendNotificationForDelete(1, currentUser.getId());
    }
}