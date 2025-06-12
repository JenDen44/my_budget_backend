package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseSummaryDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.User;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.ReportTable;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private PurchaseSummaryDTO p1;

    private PurchaseSummaryDTO p2;

    @BeforeEach
    void setup() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testuser");

        endDate = LocalDate.now();
        startDate = LocalDate.now().minusDays(5);

        p1 = PurchaseSummaryDTO.builder()
                .category(Category.EDUCATION)
                .cost(BigDecimal.valueOf(200.00))
                .quantity(2)
                .purchaseDate(startDate)
                .build();

        p2 = PurchaseSummaryDTO.builder()
                .category(Category.CLOTHING)
                .cost(BigDecimal.valueOf(100.00))
                .quantity(3)
                .purchaseDate(startDate.plusDays(2))
                .build();
    }

    @Test
    void getTableReportItemsByDate_returnsCorrectReport() {
        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<PurchaseSummaryDTO> mockPurchases = Arrays.asList(p1, p2);
        when(purchaseRepository.findPurchaseSummariesByDateRange(startDate, endDate, currentUser.getId()))
                .thenReturn(mockPurchases);

        List<ReportTable> result = reportService.getTableReportItemsByDate(startDate, endDate);

        assertEquals(2, result.size());

        Map<Category, BigDecimal> firstDayCategories = result.getFirst().getPurchasesByCategory();
        Map<Category, BigDecimal> secondCategories = result.getLast().getPurchasesByCategory();

        assertEquals(1, firstDayCategories.size());
        assertEquals(p1.getCost().multiply(BigDecimal.valueOf(p1.getQuantity())),
                firstDayCategories.get(Category.EDUCATION));

        assertEquals(1, secondCategories.size());
        assertEquals(p2.getCost().multiply(BigDecimal.valueOf(p2.getQuantity())),
                secondCategories.get(Category.CLOTHING));
    }

    @Test
    void getChartReportItemsByDate_returnsCorrectChart() {
        when(userService.getCurrentUser()).thenReturn(currentUser);

        List<PurchaseSummaryDTO> mockPurchases = Arrays.asList(p1, p2);
        when(purchaseRepository.findPurchaseSummariesByDateRange(startDate, endDate, currentUser.getId()))
                .thenReturn(mockPurchases);

        List<ReportChart> result = reportService.getChartReportItemsByDate(startDate, endDate);

        assertThat(result).hasSize(2);
        Map<Category, BigDecimal> map = new HashMap<>();
        result.forEach(reportChart -> map.put(reportChart.getCategory(), reportChart.getTotal()));

        assertThat(map.get(Category.EDUCATION)).isEqualTo(p1.getCost().multiply(BigDecimal.valueOf(p1.getQuantity())));
        assertThat(map.get(Category.CLOTHING)).isEqualTo(p2.getCost().multiply(BigDecimal.valueOf(p2.getQuantity())));
    }
}
