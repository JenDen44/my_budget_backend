package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.service.ReportService;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.ReportTable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
            summary = "Get report table data",
            description = "Retrieves tabular report data for purchases within a date range",
            parameters = {
                    @Parameter(
                            name = "startDate",
                            description = "Start date of the reporting period (yyyy-M-d)",
                            example = "2023-01-01",
                            required = true
                    ),
                    @Parameter(
                            name = "endDate",
                            description = "End date of the reporting period (yyyy-M-d)",
                            example = "2023-12-31",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Report data successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReportTable.class),
                                    examples = @ExampleObject(
                                            name = "Report Example",
                                            value = """
                                                    [
                                                      {
                                                        "date": "2023-10-01",
                                                        "purchasesByCategory": {
                                                          "FOOD": 85.25,
                                                          "TRANSPORT": 150.00,
                                                          "ENTERTAINMENT": 300.50
                                                        }
                                                      },
                                                      {
                                                        "date": "2023-10-02",
                                                        "purchasesByCategory": {
                                                          "CLOTHING": 450.00,
                                                          "FOOD": 65.75,
                                                          "HEALTH": 1200.00
                                                        }
                                                      },
                                                      {
                                                        "date": "2023-10-03",
                                                        "purchasesByCategory": {
                                                          "TRANSPORT": 85.60,
                                                          "UTILITIES": 350.25,
                                                          "EDUCATION": 750.00
                                                        }
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 401, \"message\":\"Invalid or expired token\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "ValidationErrorExample",
                                            value = """
                                                    {
                                                      "code": 422,
                                                      "message": "Validation Error",
                                                      "fields": [
                                                        {
                                                          "field": "startDate",
                                                          "message": "start date is required"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Incorrect date range",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 422, \"message\":\"Start date must be before or equal to end date\"}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("table")
    public List<ReportTable> getTableReportDataByDatePeriod(
            @RequestParam("startDate") @NotNull(message = "start date is required") @DateTimeFormat(pattern="yyyy-M-d") LocalDate startDate,
            @RequestParam("endDate") @NotNull(message = "end date is required") @DateTimeFormat(pattern="yyyy-M-d") LocalDate endDate
    ) {
        validateDateRange(startDate, endDate);
        return reportService.getTableReportItemsByDate(startDate, endDate);
    }

    @Operation(
            summary = "Get report chart data",
            description = "Retrieves chart visualization data for purchases within a date range",
            parameters = {
                    @Parameter(
                            name = "startDate",
                            description = "Start date of the reporting period (yyyy-M-d)",
                            example = "2023-01-01",
                            required = true
                    ),
                    @Parameter(
                            name = "endDate",
                            description = "End date of the reporting period (yyyy-M-d)",
                            example = "2023-12-31",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Chart data successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReportChart.class),
                                    examples = @ExampleObject(
                                            name = "Report Example",
                                            value = """
                                                    [
                                                        {
                                                          "category": "FOOD",
                                                          "total": 12550.75
                                                        },
                                                        {
                                                          "category": "TRANSPORT",
                                                          "total": 8320.50
                                                        },
                                                        {
                                                          "category": "RENT",
                                                          "total": 15000.00
                                                        }
                                                      ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 401, \"message\":\"Invalid or expired token\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "ValidationErrorExample",
                                            value = """
                                                    {
                                                      "code": 422,
                                                      "message": "Validation Error",
                                                      "fields": [
                                                        {
                                                          "field": "startDate",
                                                          "message": "start date is required"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Incorrect date range",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"code\": 422, \"message\":\"Start date must be before or equal to end date\"}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("chart")
    public List<ReportChart> getChartReportDataByDatePeriod(
            @RequestParam("startDate")  @NotNull(message = "start date is required") @DateTimeFormat(pattern="yyyy-M-d") LocalDate startDate,
            @RequestParam("endDate")  @NotNull(message = "end date is required") @DateTimeFormat(pattern="yyyy-M-d") LocalDate endDate
    ) {
        validateDateRange(startDate, endDate);
        return reportService.getChartReportItemsByDate(startDate, endDate);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before or equal to end date");
    }
}