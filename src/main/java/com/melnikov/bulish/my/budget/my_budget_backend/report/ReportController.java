package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("table")
    public ResponseEntity<List<ReportTable>> getTableReportDataByDatePeriod(@JsonFormat(pattern="y-M-d")@RequestParam("startDate") String startDate,
                                                                            @JsonFormat(pattern="y-M-d") @RequestParam("endDate") String endDate) {

            return ResponseEntity.ok(reportService.getTableReportItemsByDate(startDate,endDate));
    }

    @GetMapping("chart")
    public ResponseEntity<List<ReportChart>> getChartReportDataByDatePeriod(@JsonFormat(pattern="y-M-d")@RequestParam("startDate") String startDate,
                                                                            @JsonFormat(pattern="y-M-d") @RequestParam("endDate") String endDate) {

        return ResponseEntity.ok(reportService.getChartReportItemsByDate(startDate,endDate));
    }
}