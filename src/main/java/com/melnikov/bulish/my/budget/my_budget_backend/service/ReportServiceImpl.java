package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.interfaces.PurchaseForTableProjection;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportTable;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public  class ReportServiceImpl implements ReportService {

    private final PurchaseRepository purchaseRepository;

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ReportTable> getTableReportItemsByDate(LocalDate startDate, LocalDate endDate) {
        log.info("ReportService.getTableReportItemsByDate() started");
        var purchases = getPurchasesWithinDateRange(startDate, endDate);

        Map<LocalDate, Map<Category, Double>> map = new HashMap<>();

        for (var purchase : purchases) {
            map.computeIfAbsent(purchase.getPurchaseDate(), k -> new HashMap<>())
                    .merge(purchase.getCategory(), purchase.getTotalCost(), Double::sum);
        }

         return Collections.unmodifiableList(map.entrySet().stream()
                 .map(entry -> new ReportTable(entry.getKey(), entry.getValue()))
                 .sorted(Comparator.comparing(ReportTable::getDate))
                 .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public List<ReportChart> getChartReportItemsByDate(LocalDate startDate, LocalDate endDate) {
        log.info("ReportService.getChartReportItemsByDate() started");
        var purchases = getPurchasesWithinDateRange(startDate, endDate);

        Map<Category, Double> totalByCategory = new HashMap<>();

        for (var purchase : purchases) {
            totalByCategory.merge(purchase.getCategory(), purchase.getTotalCost(), Double::sum);
        }

        return Collections.unmodifiableList(totalByCategory.entrySet().stream()
                .map(entry -> new ReportChart(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
    }

    private List<PurchaseForTableProjection> getPurchasesWithinDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("startDate {} and endDate {}", startDate, endDate);

        var currentUser = userService.getCurrentUser();
        log.debug("current user id {}, username {}", currentUser.getId(), currentUser.getUsername());

        var purchases = purchaseRepository.findPurchaseSummariesByDateRange(startDate, endDate, currentUser.getId());
        log.debug("purchases count between startDate {} and end date {}, {} ", startDate, endDate, purchases.size());

        return purchases;
    }
}