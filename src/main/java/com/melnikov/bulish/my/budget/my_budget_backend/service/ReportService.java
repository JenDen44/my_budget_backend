package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportTable;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<ReportTable> getTableReportItemsByDate(LocalDate startDate, LocalDate endDate);

    List<ReportChart> getChartReportItemsByDate(LocalDate startDate, LocalDate endDate);
}
