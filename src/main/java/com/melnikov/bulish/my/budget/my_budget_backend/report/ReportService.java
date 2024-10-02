package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseRepository;
import com.melnikov.bulish.my.budget.my_budget_backend.user.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public  class ReportService {

    private final PurchaseRepository purchaseRepository;
    private final UserServiceImpl userService;

    @Autowired
    public ReportService(PurchaseRepository purchaseRepository, UserServiceImpl userService) {
        this.purchaseRepository = purchaseRepository;
        this.userService = userService;
    }

    public List<ReportTable> getTableReportItemsByDate(String startDate, String endDate) {
        log.debug(
            "ReportService.getTableReportItemsByDate() started with parameters : startDate {} and endDate {}",
            startDate,
            endDate
        );

        var currentUser = userService.getCurrentUser();

        log.debug("current user {} ", currentUser);

        var startTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("y-M-d"));
        var endTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("y-M-d"));
        var purchases = purchaseRepository.findPurchaseWithTimeBetween(startTime, endTime, currentUser.getId());

        log.debug("purchases between startDate {} and end date {}, {} ", startDate, endDate, purchases);

        var map = new HashMap<LocalDate, HashMap<Category, Double>>();

        for (var purchase : purchases) {
            var mapCategory = map.getOrDefault(purchase.getPurchaseDate(), new HashMap<Category, Double>());
            var total = mapCategory.getOrDefault(purchase.getCategory(),0.0);
            total += purchase.getTotalCost();

            mapCategory.put(purchase.getCategory(), total);
            map.put(purchase.getPurchaseDate(), mapCategory);
        }

        var reportTables = new ArrayList<ReportTable>();

        for (var entry: map.entrySet()) {
            reportTables.add(new ReportTable(entry.getKey(), entry.getValue()));
        }

        Collections.sort(reportTables, new Comparator<ReportTable>() {
            @Override
            public int compare(ReportTable o1, ReportTable o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

         return reportTables;
    }

    public List<ReportChart> getChartReportItemsByDate(String startDate, String endDate) {
        log.debug(
            "ReportService.getChartReportItemsByDate() started with parameters : startDate {} and endDate {}",
            startDate,
            endDate
        );

        var currentUser = userService.getCurrentUser();

        log.debug("current user {} ", currentUser);

        var startTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("y-M-d"));
        var endTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("y-M-d"));
        var purchases = purchaseRepository.findPurchaseWithTimeBetween(startTime, endTime, currentUser.getId());

        log.debug("purchases between startDate {} and end date {}, {} ", startDate, endDate, purchases);

        var totalByCategory = new HashMap<Category, Double>();

        for (var purchase : purchases) {
            double total = totalByCategory.getOrDefault(purchase.getCategory(),0.0);
            total+=purchase.getTotalCost();

            totalByCategory.put(purchase.getCategory(), total);
        }

        var reportCharts = new ArrayList<ReportChart>();

        for (var entry: totalByCategory.entrySet()) {
            reportCharts.add(new ReportChart(entry.getKey(), entry.getValue()));
        }

        return reportCharts;
    }
}