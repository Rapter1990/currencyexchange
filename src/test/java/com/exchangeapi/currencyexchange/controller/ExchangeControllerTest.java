package com.exchangeapi.currencyexchange.controller;

import com.exchangeapi.currencyexchange.base.BaseControllerTest;
import com.exchangeapi.currencyexchange.dto.ExchangeDto;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.service.ExchangeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

class ExchangeControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeService;

    @Test
    public void testGetRates() throws Exception {
        // Mock the response from the ExchangeService
        ExchangeDto exchangeDto = ExchangeDto.builder()
                .id("1")
                .base(EnumCurrency.USD)
                .amount(100.0)
                .date(LocalDate.now())
                .rates(Collections.singletonMap(EnumCurrency.EUR, 0.85))
                .build();
        when(exchangeService.calculateExchangeRate(100.0, EnumCurrency.USD, Arrays.asList(EnumCurrency.EUR)))
                .thenReturn(exchangeDto);

        // Perform the GET request
        mockMvc.perform(get("/v1/exchange")
                        .param("base", "USD")
                        .param("target", "EUR")
                        .param("amount", "100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.base", is("USD")))
                .andExpect(jsonPath("$.amount", is(100.0)))
                .andExpect(jsonPath("$.date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.rates.EUR", is(0.85)));
    }
}