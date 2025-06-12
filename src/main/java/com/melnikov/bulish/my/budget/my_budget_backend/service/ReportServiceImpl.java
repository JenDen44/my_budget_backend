package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.PurchaseSummaryDTO;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.ReportTable;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public  class ReportServiceImpl implements ReportService {

    private final PurchaseRepository purchaseRepository;

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ReportTable> getTableReportItemsByDate(LocalDate startDate, LocalDate endDate) {
        log.info("ReportServiceImpl.getTableReportItemsByDate() started");
        var purchases = getPurchasesWithinDateRange(startDate, endDate);

        Map<LocalDate, Map<Category, BigDecimal>> map = new HashMap<>();
        for (var purchase : purchases) {
            map.computeIfAbsent(purchase.getPurchaseDate(), k -> new HashMap<>()).merge(purchase.getCategory(),
                    purchase.getCost().multiply(BigDecimal.valueOf(purchase.getQuantity())), BigDecimal::add);
        }

         return map.entrySet().stream()
                 .map(entry -> new ReportTable(entry.getKey(), entry.getValue()))
                 .sorted(Comparator.comparing(ReportTable::getDate))
                 .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportChart> getChartReportItemsByDate(LocalDate startDate, LocalDate endDate) {
        log.info("ReportService.getChartReportItemsByDate() started");
        var purchases = getPurchasesWithinDateRange(startDate, endDate);

        Map<Category, BigDecimal> totalByCategory = new HashMap<>();

        for (var purchase : purchases) {
            totalByCategory.merge(purchase.getCategory(),
                    purchase.getCost().multiply(BigDecimal.valueOf(purchase.getQuantity())), BigDecimal::add);
        }

        return totalByCategory.entrySet().stream()
                .map(entry -> new ReportChart(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<PurchaseSummaryDTO> getPurchasesWithinDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("startDate {} and endDate {}", startDate, endDate);

        var currentUser = userService.getCurrentUser();
        log.debug("current user {}", currentUser);

        return purchaseRepository.findPurchaseSummariesByDateRange(startDate, endDate, currentUser.getId());
    }
}