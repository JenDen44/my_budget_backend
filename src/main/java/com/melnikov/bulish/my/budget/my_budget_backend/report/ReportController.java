package com.melnikov.bulish.my.budget.my_budget_backend.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(
        description = "Endpoint for get report table",
        summary = "If you need to get report table for all purchases by specific date for the current user, please use this endpoint",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Unauthorized/Invalid token",
                responseCode = "401"
            ),
            @ApiResponse(
                description = "Validation error",
                responseCode = "422"
            )
        }
    )
    @GetMapping("table")
    public ResponseEntity<List<ReportTable>> getTableReportDataByDatePeriod(
        @JsonFormat(pattern="y-M-d") @RequestParam("startDate") String startDate,
        @JsonFormat(pattern="y-M-d") @RequestParam("endDate") String endDate
    ) {
        return ResponseEntity.ok(reportService.getTableReportItemsByDate(startDate,endDate));
    }

    @Operation(
        description = "Endpoint for get report chart",
        summary = "If you need to get report chart for all purchases by specific date for the current user, please use this endpoint",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized/Invalid token",
                    responseCode = "401"
            ),
            @ApiResponse(
                description = "Validation error",
                responseCode = "422"
            )
        }
    )
    @GetMapping("chart")
    public ResponseEntity<List<ReportChart>> getChartReportDataByDatePeriod(
        @JsonFormat(pattern="y-M-d") @RequestParam("startDate") String startDate,
        @JsonFormat(pattern="y-M-d") @RequestParam("endDate") String endDate
    ) {
        return ResponseEntity.ok(reportService.getChartReportItemsByDate(startDate,endDate));
    }
}