package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportTable;

import java.util.List;

public interface ReportService {

    List<ReportTable> getTableReportItemsByDate(String startDate, String endDate);

    List<ReportChart> getChartReportItemsByDate(String startDate, String endDate);
}
