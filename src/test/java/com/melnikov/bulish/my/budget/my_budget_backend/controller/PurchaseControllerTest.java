package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.bulish.my.budget.my_budget_backend.enums.Category;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PagedResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseDto;
import com.melnikov.bulish.my.budget.my_budget_backend.model.PurchaseRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.service.AuthenticationService;
import com.melnikov.bulish.my.budget.my_budget_backend.service.JwtTokenService;
import com.melnikov.bulish.my.budget.my_budget_backend.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PurchaseControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseService purchaseService;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    AuthenticationService authenticationService;

    private static final String BASE_URL = "/purchases";

    private PurchaseRequest createSamplePurchaseRequest() {
        return new PurchaseRequest(Category.CLOTHE, 123.80, 2, LocalDate.now());
    }

    private PurchaseDto createSamplePurchaseDto() {
        PurchaseDto purchaseDto = new PurchaseDto(Category.CLOTHE, 123.80, 2, LocalDate.now());
        purchaseDto.setUserId(1);
        return purchaseDto;
    }

    private PagedResponse<PurchaseDto> createSamplePagedResponse() {
        PagedResponse<PurchaseDto> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent((List.of(new PurchaseDto(), new PurchaseDto())));
        return pagedResponse;
    }

    @Test
    public void getPurchasePage() throws Exception {
        var pagedResponse = createSamplePagedResponse();

        when(purchaseService.getPurchasesForCurrentUser(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pagedResponse);

        mockMvc.perform(get(BASE_URL)
                .param("pageNo", "0")
                .param("pageSize", "5")
                .param("sortBy", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    public void createPurchase() throws Exception {
        var purchaseDto = createSamplePurchaseDto();
        var purchaseRequest = createSamplePurchaseRequest();

        when(purchaseService.savePurchase(any(PurchaseRequest.class))).thenReturn(purchaseDto);

         mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseRequest))
                .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.category").value(purchaseDto.getCategory()))
                .andExpect(jsonPath("$.id").value(purchaseDto.getId()))
                .andExpect(jsonPath("$.cost").value(purchaseDto.getCost()));
        }

    @Test
    public void updatePurchase() throws Exception {
        var purchaseDto = createSamplePurchaseDto();

        when(purchaseService.updatePurchase(any(PurchaseDto.class), anyInt())).thenReturn(purchaseDto);

        mockMvc.perform(put(BASE_URL + "/" + purchaseDto.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(purchaseDto))
                .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.category").value(purchaseDto.getCategory()))
                .andExpect(jsonPath("$.id").value(purchaseDto.getId()))
                .andExpect(jsonPath("$.cost").value(purchaseDto.getCost()));
    }


    @Test
    public void deletePurchase() throws Exception {
        var purchaseDto = createSamplePurchaseDto();

        mockMvc.perform(delete(BASE_URL + "/" + purchaseDto.getId())
                .with(csrf()))
                .andExpect(status()
                .isOk());

        verify(purchaseService).deletePurchase(anyInt());
    }
}