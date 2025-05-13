package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.exception.ValidationException;
import com.melnikov.bulish.my.budget.my_budget_backend.interfaces.PurchaseForTableProjection;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportTable;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public  class ReportServiceImpl implements ReportService {

    private final PurchaseRepository purchaseRepository;
    private final UserServiceImpl userService;

    public List<ReportTable> getTableReportItemsByDate(String startDate, String endDate) {
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

    public List<ReportChart> getChartReportItemsByDate(String startDate, String endDate) {
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

    private LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-M-d"));
    }

    private List<PurchaseForTableProjection> getPurchasesWithinDateRange(String startDate, String endDate) {
        log.info("startDate {} and endDate {}", startDate, endDate);
        LocalDate startTime;
        LocalDate endTime;

        try {
            startTime = parseDate(startDate);
            endTime = parseDate(endDate);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}, {}", startDate, endDate);
            throw new ValidationException("Report", e.getMessage());
        }

        var currentUser = userService.getCurrentUser();
        log.debug("current user id {}, username {}", currentUser.getId(), currentUser.getUsername());

        var purchases = purchaseRepository.findPurchaseSummariesByDateRange(startTime, endTime, currentUser.getId());
        log.debug("purchases count between startDate {} and end date {}, {} ", startDate, endDate, purchases.size());

        return purchases;
    }
}