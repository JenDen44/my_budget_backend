package com.melnikov.bulish.my.budget.my_budget_backend.report;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "test", password = "test")
    public void findTableReportItemsByDate() throws Exception {
        var startDate = "2014-09-01";
        var endDate = "2027-09-30";
        var requestURL = "/reports/table";

         mockMvc.perform(get(requestURL)
             .param("startDate",startDate)
             .param("endDate",endDate))
             .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void findChartReportItemsByDate() throws Exception {
        var startDate = "2014-09-01";
        var endDate = "2027-09-30";
        var requestURL = "/reports/chart";

        mockMvc.perform(get(requestURL)
            .param("startDate",startDate)
            .param("endDate",endDate))
            .andExpect(status().isOk()).andDo(print());
    }
}
