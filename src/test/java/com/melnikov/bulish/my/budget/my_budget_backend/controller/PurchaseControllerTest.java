package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.entity.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseDto;
import com.melnikov.bulish.my.budget.my_budget_backend.repository.PurchaseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseRepository repo;

    private static final String BASE_URL = "/purchases";

    private Integer testPurchaseId;

    @AfterEach
    public void cleanup() {
        if (testPurchaseId != null && repo.existsById(testPurchaseId)) {
            repo.deleteById(testPurchaseId);
        }
        testPurchaseId = null;
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void findAllPurchases() throws Exception {
        createTestPurchase();

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void createPurchase() throws Exception {
            var purchaseResponse = new PurchaseDto(Category.CLOTHE, 123.80, 2, LocalDate.now());
            var result = mockMvc.perform(
                    post(BASE_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(purchaseResponse))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

            var created = objectMapper.readValue(result.getResponse().getContentAsString(), PurchaseDto.class);

            assertThat(created.getId()).isNotNull();
            assertThat(created.getCategory()).isEqualTo(Category.CLOTHE);
            assertThat(created.getCost()).isEqualTo(123.80);

            repo.deleteById(created.getId());

        }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void createPurchaseInvalidRequest() throws Exception {
        String invalidJson = "{}";
        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(invalidJson)
                        .with(csrf()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void updatePurchase() throws Exception {
        Integer id = createTestPurchase();
        var today = LocalDate.now();
        var purchaseResponse = new PurchaseDto(Category.FOOD, 123.80, 2, today);
        var result = mockMvc.perform(
                put(BASE_URL + "/" + id)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(purchaseResponse))
                    .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        var updated = objectMapper.readValue(result.getResponse().getContentAsString(), Purchase.class);

        Optional<PurchaseDto> fromDbOpt = repo.findById(id).map(PurchaseDto::new);
        assertThat(fromDbOpt).isPresent();
        assertThat(fromDbOpt.get().getCategory()).isEqualTo(Category.FOOD);
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void updatePurchaseInvalidRequest() throws Exception {
        Integer id = createTestPurchase();
        String invalidJson = "{}";
        mockMvc.perform(put(BASE_URL + "/" + id)
                        .contentType("application/json")
                        .content(invalidJson)
                        .with(csrf()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void deletePurchase() throws Exception {
        Integer id = createTestPurchase();

        mockMvc.perform(delete(BASE_URL + "/" + id).with(csrf()))
                .andExpect(status().isOk());

        assertThat(repo.existsById(id)).isFalse();
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void deletePurchaseNotFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/999999").with(csrf()))
                .andExpect(status().isNotFound());
    }

    private Integer createTestPurchase() throws Exception {
        PurchaseDto purchaseDto = new PurchaseDto(Category.CLOTHE, 123.80, 2, LocalDate.now());
        String jsonContent = objectMapper.writeValueAsString(purchaseDto);

        String responseContent = mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(jsonContent)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PurchaseDto created = objectMapper.readValue(responseContent, PurchaseDto.class);
        this.testPurchaseId = created.getId();
        return created.getId();
    }

    @Test
    @WithMockUser(username = "test", password = "test")
    public void getPurchasePage() throws Exception {
        createTestPurchase();
        mockMvc.perform(get(BASE_URL)
                        .param("pageNo", "0")
                        .param("pageSize", "5")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}