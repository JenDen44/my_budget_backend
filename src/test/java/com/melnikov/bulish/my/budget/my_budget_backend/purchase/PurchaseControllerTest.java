package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseRepository repo;

    @Test
    @WithMockUser(username = "test", password = "test")
    public void findAllPurchases() throws Exception {
        var url = "/purchases";
        var result = mockMvc.perform(get(url))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        var jsonResponse = result.getResponse().getContentAsString();
        var purchases = objectMapper.readValue(jsonResponse, Purchase[].class);

        assertThat(purchases).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void createPurchase() throws Exception {
            var url = "/purchases";
            var today = LocalDate.now();
            var purchaseResponse = new PurchaseDto(Category.CLOTHE, 123.80, 2, today);
            var result = mockMvc.perform(
                    post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(purchaseResponse))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            var response = result.getResponse().getContentAsString();
            var purchaseFromResponse = objectMapper.readValue(response, Purchase.class);
            var findById = repo.findById(purchaseFromResponse.getId());

            assertThat(findById.isPresent());

        }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void updatePurchase() throws Exception {
        var purchaseId = 8;
        var url = "/purchases/" + purchaseId;
        var today = LocalDate.now();
        var purchaseResponse = new PurchaseDto(Category.FOOD, 123.80, 2, today);
        var result = mockMvc.perform(
                put(url).contentType("application/json")
                    .content(objectMapper.writeValueAsString(purchaseResponse))
                    .with(csrf())
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        var response = result.getResponse().getContentAsString();
        var purchaseFromResponse = objectMapper.readValue(response, Purchase.class);
        var findById = repo.findById(purchaseId);

        assertThat(purchaseFromResponse.getId().equals(purchaseId));
        assertThat(findById.isPresent());
        assertThat(findById.get().getCategory().equals(Category.FOOD));

    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void deletePurchase() throws Exception {
        var purchaseId = 8;
        var url = "/purchases/" + purchaseId;

        mockMvc.perform(delete(url)).andExpect(status().isOk());

        var findById = repo.findById(purchaseId);

        assertThat(findById).isNotPresent();
    }
}