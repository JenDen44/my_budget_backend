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
import static org.mockito.ArgumentMatchers.*;
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
    private Purchase p1 = null;
    private Purchase p2 = null;
    private PurchaseDto dto = null;


    @BeforeEach
    void setup() {
        dto = new PurchaseDto(null, 100.00, 3, LocalDate.now());
        p1 = new Purchase(1, 100.00, 2, currentUser);
        p2 = new Purchase(2, 100.00, 2, currentUser);
        currentUser = new User(1, "testuser");
    }

    private PurchaseRequest createSamplePurchaseRequest() {
        PurchaseRequest req = new PurchaseRequest();
        req.setCost(50.00);
        req.setQuantity(2);
        req.setCategory(null);
        req.setPurchaseDate(LocalDate.now());
        return req;
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
        PurchaseRequest req = createSamplePurchaseRequest();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepo.save(any(Purchase.class))).thenReturn(p2);

        PurchaseDto result = service.savePurchase(req);
        assertThat(result).isNotNull();
        verify(purchaseRepo).save(any(Purchase.class));
        verify(notificationService).sendNotificationForCreate(any(PurchaseDto.class), eq(currentUser.getId()));
    }

    @Test
    void updatePurchase() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepo.findById(1)).thenReturn(Optional.of(p2));
        when(purchaseRepo.save(any(Purchase.class))).thenAnswer(i -> i.getArguments()[0]);

        PurchaseDto result = service.updatePurchase(dto, dto.getId());
        assertThat(result).isNotNull();
        assertThat(result.getCost()).isEqualTo(100);
        verify(purchaseRepo).save(any(Purchase.class));
        verify(notificationService).sendNotificationForUpdate(any(PurchaseDto.class), eq(currentUser.getId()));
    }

    @Test
    void deletePurchase() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepo.findById(anyInt())).thenReturn(Optional.of(p1));

        service.deletePurchase(p1.getId());
        verify(purchaseRepo).deleteById(anyInt());
        verify(notificationService).sendNotificationForDelete(p1.getId(), currentUser.getId());
    }
}