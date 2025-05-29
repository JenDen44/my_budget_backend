package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PagedResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseDto;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.service.AuthenticationServiceImpl;
import com.melnikov.bulish.my.budget.my_budget_backend.service.JwtTokenServiceImpl;
import com.melnikov.bulish.my.budget.my_budget_backend.service.PurchaseService;
import com.melnikov.bulish.my.budget.my_budget_backend.service.PurchaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PurchaseServiceImpl purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    private PurchaseDto purchaseDto;
    private PurchaseRequest purchaseRequest;
    private PagedResponse<PurchaseDto> pagedResponse;

    private static String URL = "/purchases";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(purchaseController).build();

        purchaseDto = PurchaseDto.builder()
                .id(1)
                .category(Category.CLOTHE)
                .cost(25.50)
                .build();

        purchaseRequest = PurchaseRequest.builder()
                .category(Category.EDUCATION)
                .cost(15.75)
                .build();

        pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(List.of(purchaseDto));
        pagedResponse.setPageNumber(0);
        pagedResponse.setPageSize(10);
        pagedResponse.setTotalElements(1);
        pagedResponse.setTotalPages(1);
    }

    @Test
    void getPurchasePage() throws Exception {
        when(purchaseService.getPurchasesForCurrentUser(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pagedResponse);

        mockMvc.perform(get(URL)
                        .param("pageNo", "0")
                        .param("pageSize", "10")
                        .param("sortBy", "date")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].category").value("CLOTHE"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getPurchase() throws Exception {
        when(purchaseService.findPurchaseDtoById(anyInt()))
                .thenReturn(purchaseDto);

        mockMvc.perform(get(URL+"/" + purchaseDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("CLOTHE"))
                .andExpect(jsonPath("$.cost").value(25.50));
    }

    @Test
    void createPurchase() throws Exception {
        when(purchaseService.savePurchase(any(PurchaseRequest.class)))
                .thenReturn(purchaseDto);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("CLOTHE"))
                .andExpect(jsonPath("$.cost").value(25.50));
    }

    @Test
    void updatePurchase() throws Exception {
        PurchaseDto updatedDto = PurchaseDto.builder()
                .id(1)
                .category(Category.FOOD)
                .cost(30.00)
                .build();

        when(purchaseService.updatePurchase(any(PurchaseDto.class), anyInt()))
                .thenReturn(updatedDto);

        mockMvc.perform(put(URL+"/" + updatedDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.cost").value(30.00));
    }

    @Test
    void deletePurchase() throws Exception {
        doNothing().when(purchaseService).deletePurchase(anyInt());

        mockMvc.perform(delete(URL + "/" + purchaseDto.getId()))
                .andExpect(status().isOk());
    }
}
