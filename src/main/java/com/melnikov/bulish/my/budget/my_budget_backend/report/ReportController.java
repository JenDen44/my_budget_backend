package com.melnikov.bulish.my.budget.my_budget_backend.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("table/{startDate}/{endDate}")
    public ResponseEntity<List<ReportTable>> getTableReportDataByDatePeriod(@PathVariable("startDate") String startDate,
                                                                       @PathVariable("endDate") String endDate) {

            return ResponseEntity.ok(reportService.getTableReportItemsByDate(startDate,endDate));
    }

    @GetMapping("chart/{startDate}/{endDate}")
    public ResponseEntity<List<ReportChart>> getChartReportDataByDatePeriod(@PathVariable("startDate") String startDate,
                                                                       @PathVariable("endDate") String endDate) {

        return ResponseEntity.ok(reportService.getChartReportItemsByDate(startDate,endDate));
    }
}