package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.interfaces.PurchaseForTableProjection;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportTable;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User currentUser;

    private LocalDate startDate = null;

    private LocalDate endDate = null;

    @BeforeEach
    void setup() {
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setUsername("testuser");
        endDate = LocalDate.now();
        startDate = LocalDate.now().minusDays(5);
    }

    @Test
    void getTableReportItemsByDate_returnsCorrectReport() {
        PurchaseForTableProjection p1 = mock(PurchaseForTableProjection.class);
        when(p1.getPurchaseDate()).thenReturn(LocalDate.of(2023,1,10));
        when(p1.getCategory()).thenReturn(Category.FOOD);
        when(p1.getTotalCost()).thenReturn(100.0);

        PurchaseForTableProjection p2 = mock(PurchaseForTableProjection.class);
        when(p2.getPurchaseDate()).thenReturn(LocalDate.of(2023,1,10));
        when(p2.getCategory()).thenReturn(Category.CLOTHE);
        when(p2.getTotalCost()).thenReturn(150.0);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<PurchaseForTableProjection> mockPurchases = Arrays.asList(p1, p2);
        when(purchaseRepository.findPurchaseSummariesByDateRange(startDate, endDate, currentUser.getId()))
                .thenReturn(mockPurchases);

        List<ReportTable> result = reportService.getTableReportItemsByDate(startDate, endDate);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDate()).isEqualTo(p1.getPurchaseDate());
    }

    @Test
    void getChartReportItemsByDate_returnsCorrectChart() {
        PurchaseForTableProjection p1 = mock(PurchaseForTableProjection.class);
        when(p1.getCategory()).thenReturn(Category.FOOD);
        when(p1.getTotalCost()).thenReturn(300.0);

        PurchaseForTableProjection p2 = mock(PurchaseForTableProjection.class);
        when(p2.getCategory()).thenReturn(Category.CLOTHE);
        when(p2.getTotalCost()).thenReturn(150.0);

        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<PurchaseForTableProjection> mockPurchases = Arrays.asList(p1, p2);
        when(purchaseRepository.findPurchaseSummariesByDateRange(startDate, endDate, currentUser.getId()))
                .thenReturn(mockPurchases);

        List<ReportChart> result = reportService.getChartReportItemsByDate(startDate, endDate);

        assertThat(result).hasSize(2);
        Map<Category, Double> map = new HashMap<>();
        result.forEach(reportChart -> map.put(reportChart.getCategory(), reportChart.getTotal()));

        assertThat(map.get(Category.FOOD)).isEqualTo(300.0);
        assertThat(map.get(Category.CLOTHE)).isEqualTo(150.0);
    }
}