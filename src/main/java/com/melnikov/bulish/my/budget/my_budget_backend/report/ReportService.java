package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseNotFoundException;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public  class ReportService {
    private final PurchaseRepository purchaseRepository;

    @Autowired
    public ReportService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<ReportTable> getTableReportItemsByDate(String startDate, String endDate) {
        Map<LocalDate, Map<Category, Double>> map = new HashMap<>();
        List<ReportTable> reportTables = new ArrayList<>();

        LocalDate startTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("y-M-d"));
        LocalDate endTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("y-M-d"));

        List<Purchase> purchases = purchaseRepository.findPurchaseWithTimeBetween(startTime, endTime);
        if (purchases.isEmpty()) throw new PurchaseNotFoundException("No purchases were found between "
                + startTime + " " + endDate);

        for (Purchase purchase : purchases) {
            Map<Category,Double> mapCategory = map.getOrDefault(purchase.getPurchaseDate(), new HashMap<>());
            double total = mapCategory.getOrDefault(purchase.getCategory(),0.0);
            total+=purchase.getTotalCost();
            mapCategory.put(purchase.getCategory(), total);
            map.put(purchase.getPurchaseDate(), mapCategory);
        }

        for (Map.Entry<LocalDate, Map<Category, Double>> entry: map.entrySet()) {
            reportTables.add(new ReportTable(entry.getKey(), entry.getValue()));
        }
        return reportTables;
    }

    public ReportChart getChartReportItemsByDate(String startDate, String endDate) {
        Map<Category, Double> totalByCategory = new HashMap<>();

        LocalDate startTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("y-M-d"));
        LocalDate endTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("y-M-d"));

        List<Purchase> purchases = purchaseRepository.findPurchaseWithTimeBetween(startTime, endTime);
        if (purchases.isEmpty()) throw new PurchaseNotFoundException("No purchases were found between "
                + startTime + " " + endDate);

        for (Purchase purchase : purchases) {
            double total = totalByCategory.getOrDefault(purchase.getCategory(),0.0);
            total+=purchase.getTotalCost();
            totalByCategory.put(purchase.getCategory(), total);
        }
        ReportChart reportChart = new ReportChart(totalByCategory);

        return reportChart;
    }
}