package com.melnikov.bulish.my.budget.my_budget_backend.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{startDate}/{endDate}")
    public ResponseEntity<List<ReportItemByDate>> getReportDataByDatePeriod(@PathVariable("startDate") String startDate,
                                                                           @PathVariable("endDate") String endDate) throws ParseException {

        LocalDate startTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("y-M-d"));
        LocalDate endTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("y-M-d"));

            return ResponseEntity.ok(reportService.getReportItemsByDate(startTime,endTime));
    }
}