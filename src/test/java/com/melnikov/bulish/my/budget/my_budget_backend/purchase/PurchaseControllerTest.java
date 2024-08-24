package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.Optional;

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
        String url = "/purchases";

        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Purchase[] purchases = objectMapper.readValue(jsonResponse, Purchase[].class);

        assertThat(purchases).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void createPurchase() throws JsonProcessingException, Exception {
            String url = "/purchases";
            PurchaseDto purchaseDto = new PurchaseDto(Category.CLOTHE, 123.80, 2, new Date());

            MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                            .content(objectMapper.writeValueAsString(purchaseDto))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            Purchase purchaseFromResponse = objectMapper.readValue(response, Purchase.class);

            Optional<Purchase> findById = repo.findById(purchaseFromResponse.getId());
            assertThat(findById.isPresent());

        }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void updatePurchase() throws JsonProcessingException, Exception {
        Integer purchaseId = 8;
        String url = "/purchases/" + purchaseId;

        PurchaseDto purchaseDto = new PurchaseDto(Category.FOOD, 123.80, 2, new Date());

        MvcResult result = mockMvc.perform(put(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(purchaseDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Purchase purchaseFromResponse = objectMapper.readValue(response, Purchase.class);

        Optional<Purchase> findById = repo.findById(purchaseId);
        assertThat(findById.isPresent());

        assertThat(findById.get().getCategory().equals(Category.FOOD));

    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void deletePurchase() throws Exception {
        Integer purchaseId = 8;
        String url = "/purchases/" + purchaseId;
        mockMvc.perform(delete(url)).andExpect(status().isOk());

        Optional<Purchase> findById = repo.findById(purchaseId);

        assertThat(findById).isNotPresent();
    }
}