package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.ReportTable;
import com.melnikov.bulish.my.budget.my_budget_backend.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private LocalDate startDate;
    private LocalDate endDate;
    private ReportTable reportTable;
    private ReportChart reportChart;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();

        startDate = LocalDate.now().minusDays(6);
        endDate = LocalDate.now();

        reportTable = new ReportTable();
        reportTable.setDate(LocalDate.now().minusDays(2));
        reportTable.setPurchasesByCategory(Map.of
                (
                Category.FOOD, BigDecimal.valueOf(200.00),
                Category.EDUCATION, BigDecimal.valueOf(10000.00)
                ));

        reportChart = new ReportChart();
        reportChart.setCategory(Category.CLOTHING);
        reportChart.setTotal(BigDecimal.valueOf(850.50));
    }

    @Test
    void getTableReport() throws Exception {
        List<ReportTable> reportTables = List.of(reportTable);

        when(reportService.getTableReportItemsByDate(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reportTables);

        mockMvc.perform(get("/reports/table")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").exists())
                .andExpect(jsonPath("$[0].purchasesByCategory").isMap());
    }

    @Test
    void getChartReportData() throws Exception {
        List<ReportChart> reportCharts = List.of(reportChart);

        when(reportService.getChartReportItemsByDate(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reportCharts);

        mockMvc.perform(get("/reports/chart")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("CLOTHING"))
                .andExpect(jsonPath("$[0].total").value(850.50));
    }
}
