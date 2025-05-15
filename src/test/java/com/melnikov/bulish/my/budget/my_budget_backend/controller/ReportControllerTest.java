package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportChart;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ReportTable;
import com.melnikov.bulish.my.budget.my_budget_backend.service.AuthenticationService;
import com.melnikov.bulish.my.budget.my_budget_backend.service.JwtTokenService;
import com.melnikov.bulish.my.budget.my_budget_backend.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReportControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private ReportService reportService;
    private static final String URL_REPORT_TABLE = "/reports/table";
    private static final String URL_REPORT_CHART = "/reports/chart";

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");

    private List<ReportTable> createListReportTables() {
        return List.of(new ReportTable(), new ReportTable());
    }

    private List<ReportChart> createListReportCharts() {
        return List.of(new ReportChart(), new ReportChart());
    }

    @Test
    public void findTableReportItemsByDate() throws Exception {
        var startDate = timeFormatter.format(LocalDate.now().minusDays(2));
        var endDate = timeFormatter.format(LocalDate.now());
        var reportTables = createListReportTables();

        when(reportService.getTableReportItemsByDate(any(), any())).thenReturn(reportTables);

         mockMvc.perform(get(URL_REPORT_TABLE)
                 .param("startDate", startDate)
                 .param("endDate", endDate))
                 .andExpect(status().isOk())
                 .andDo(print())
                 .andExpect(jsonPath("$.content").isArray())
                 .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    public void findChartReportItemsByDate() throws Exception {
        var startDate = timeFormatter.format(LocalDate.now().minusDays(2));
        var endDate = timeFormatter.format(LocalDate.now());
        var reportTables = createListReportCharts();

        when(reportService.getChartReportItemsByDate(any(), any())).thenReturn(createListReportCharts());

        mockMvc.perform(get(URL_REPORT_CHART)
                .param("startDate",startDate)
                .param("endDate",endDate))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }
}
