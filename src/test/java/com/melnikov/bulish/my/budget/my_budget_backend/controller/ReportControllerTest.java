package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String URL_REPORT_TABLE = "/reports/table";
    private static final String URL_REPORT_CHART = "/reports/chart";

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");

    @Test
    @WithMockUser(username = "test", password = "test")
    public void findTableReportItemsByDate() throws Exception {
        var startDate = timeFormatter.format(LocalDate.now().minusDays(2));
        var endDate = timeFormatter.format(LocalDate.now());

         mockMvc.perform(get(URL_REPORT_TABLE)
             .param("startDate", startDate)
             .param("endDate", endDate))
             .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void findTableReportItemsByDateBadRequest() throws Exception {
        var startDate = timeFormatter.format(LocalDate.now().minusDays(2));
        var endDate = "invalid-date";

        mockMvc.perform(get(URL_REPORT_TABLE)
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void findChartReportItemsByDate() throws Exception {
        var startDate = timeFormatter.format(LocalDate.now().minusDays(2));
        var endDate = timeFormatter.format(LocalDate.now());

        mockMvc.perform(get(URL_REPORT_CHART)
            .param("startDate",startDate)
            .param("endDate",endDate))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void findChartReportItemsByDateBadRequest() throws Exception {
        var startDate = timeFormatter.format(LocalDate.now().minusDays(2));
        var endDate = "invalid-date";

        mockMvc.perform(get(URL_REPORT_CHART)
                        .param("startDate",startDate)
                        .param("endDate",endDate))
                .andExpect(status().isBadRequest());
    }
}
