package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public  class ReportService {
    private final PurchaseRepository purchaseRepository;

    @Autowired
    public ReportService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<ReportTable> getTableReportItemsByDate(String startDate, String endDate) {
        List<ReportTable> reportItemByDates = new ArrayList<>();

        LocalDate startTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("y-M-d"));
        LocalDate endTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("y-M-d"));

        List<Purchase> purchases = purchaseRepository.findPurchaseWithTimeBetween(startTime, endTime);
        if (purchases.isEmpty()) throw new ReportItemNotFoundException("No purchases were found between "
                + startTime + " " + endDate);

        Map<LocalDate, Map<Category, List<Purchase>>> categorizedBCategory2 = purchases.stream()
                .collect(Collectors.groupingBy(Purchase::getPurchaseDate,
                        Collectors.groupingBy(Purchase::getCategory)));

        for (Map.Entry<LocalDate, Map<Category, List<Purchase>>> entry : categorizedBCategory2.entrySet()) {
            for (Map.Entry<Category, List<Purchase>> innerEntry : entry.getValue().entrySet()) {
                Map<Category, Double> totalByCategory = new HashMap<>();
                Optional<Double> total = innerEntry.getValue().stream().map(a -> a.getTotalCost()).reduce((a, b) -> a + b);
                totalByCategory.put(innerEntry.getKey(), total.get());
                ReportTable reportItemByDate = new ReportTable(entry.getKey(), totalByCategory);
                reportItemByDates.add(reportItemByDate);
            }
        }
        return reportItemByDates;
    }

    public List<ReportChart> getChartReportItemsByDate(String startDate, String endDate) {
       List<ReportChart> reportChartsItems = new ArrayList<>();
        Map<Category, Double> totalByCategory = new HashMap<>();

        LocalDate startTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("y-M-d"));
        LocalDate endTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("y-M-d"));

        List<Purchase> purchases = purchaseRepository.findPurchaseWithTimeBetween(startTime, endTime);
        if (purchases.isEmpty()) throw new ReportItemNotFoundException("No purchases were found between "
                + startTime + " " + endDate);

        Map<Category,List<Purchase>> mapPurchasesByCategory = purchases.stream()
                .collect(Collectors.groupingBy(Purchase::getCategory));

        for (Map.Entry<Category, List<Purchase>> entry: mapPurchasesByCategory.entrySet()) {
            Optional<Double> total = entry.getValue().stream().map(a -> a.getTotalCost()).reduce((a, b) -> a + b);
            totalByCategory.put(entry.getKey(),total.get());
            ReportChart reportChart = new ReportChart(totalByCategory);
            reportChartsItems.add(reportChart);
        }
        return reportChartsItems;
    }
}