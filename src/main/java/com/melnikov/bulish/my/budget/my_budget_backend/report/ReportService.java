package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public  class ReportService {
    private final PurchaseRepository purchaseRepository;
    private final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public ReportService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<ReportItemByDate> getReportItemsByDate(LocalDate startDate, LocalDate endDate) {
        List<Purchase> purchases = purchaseRepository.findPurchaseWithTimeBetween(startDate, endDate);

            return mapPurchasesByCategoryAndDate(purchases);
    }
    private  List<ReportItemByDate> mapPurchasesByCategoryAndDate(List<Purchase> purchases) {
        List<ReportItemByDate> reportItemByDates = new ArrayList<>();

        Map<LocalDate, Map<Category, List<Purchase>>> categorizedBCategory2 = purchases.stream()
                .collect(Collectors.groupingBy(Purchase::getPurchaseDate,
                                Collectors.groupingBy(Purchase::getCategory)));

        for (Map.Entry<LocalDate, Map<Category, List<Purchase>>> entry : categorizedBCategory2.entrySet()) {
            for (Map.Entry<Category, List<Purchase>> innerEntry : entry.getValue().entrySet()) {
                Map<Category, Double> totalByCategory = new HashMap<>();
                Optional<Double> total = innerEntry.getValue().stream().map(a -> a.getTotalCost()).reduce((a, b) -> a + b);
                totalByCategory.put(innerEntry.getKey(), total.get());
                ReportItemByDate reportItemByDate = new ReportItemByDate(entry.getKey(), totalByCategory);
                reportItemByDates.add(reportItemByDate);
            }
        }
        return reportItemByDates;
    }
}