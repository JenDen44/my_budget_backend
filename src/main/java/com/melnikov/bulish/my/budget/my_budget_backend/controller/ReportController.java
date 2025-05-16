package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.service.ReportService;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportTable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports")
@RequiredArgsConstructor
@Validated
public class ReportController {

    private final ReportService reportService;

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
    public List<ReportTable> getTableReportDataByDatePeriod(
            @RequestParam("startDate") @NotNull @DateTimeFormat(pattern="yyyy-M-d") LocalDate startDate,
            @RequestParam("endDate") @NotNull @DateTimeFormat(pattern="yyyy-M-d")  LocalDate endDate
    ) {
        return reportService.getTableReportItemsByDate(startDate,endDate);
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
    public List<ReportChart> getChartReportDataByDatePeriod(
            @RequestParam("startDate") @NotNull @DateTimeFormat(pattern="yyyy-M-d") LocalDate startDate,
            @RequestParam("endDate") @NotNull @DateTimeFormat(pattern="yyyy-M-d") LocalDate endDate
    ) {
        return reportService.getChartReportItemsByDate(startDate,endDate);
    }
}