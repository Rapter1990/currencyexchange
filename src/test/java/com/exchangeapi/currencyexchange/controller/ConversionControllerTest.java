package com.exchangeapi.currencyexchange.controller;

import com.exchangeapi.currencyexchange.base.BaseControllerTest;
import com.exchangeapi.currencyexchange.dto.ExchangeDto;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.service.ExchangeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ConversionControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeService;

    @Test
    public void testGetConversion() throws Exception {
        // Mock the response from the ExchangeService
        ExchangeDto exchangeDto = ExchangeDto.builder()
                .id("1")
                .base(EnumCurrency.USD)
                .amount(100.0)
                .date(LocalDate.now())
                .rates(Collections.singletonMap(EnumCurrency.EUR, 0.85))
                .build();

        when(exchangeService.getConversion("1")).thenReturn(exchangeDto);

        mockMvc.perform(get("/v1/conversion/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.base", is("USD")))
                .andExpect(jsonPath("$.amount", is(100.0)))
                .andExpect(jsonPath("$.date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.rates.EUR", is(0.85)));
    }

    @Test
    public void testGetConversionList() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        ExchangeDto exchangeDto1 = ExchangeDto.builder()
                .id("123")
                .base(EnumCurrency.USD)
                .amount(100.0)
                .date(LocalDate.now())
                .rates(Collections.singletonMap(EnumCurrency.EUR, 0.85))
                .build();

        ExchangeDto exchangeDto2 = ExchangeDto.builder()
                .id("456")
                .base(EnumCurrency.EUR)
                .amount(200.0)
                .date(LocalDate.now())
                .rates(Collections.singletonMap(EnumCurrency.USD, 1.2))
                .build();

        List<ExchangeDto> exchangeDtoList = Arrays.asList(exchangeDto1, exchangeDto2);

        when(exchangeService.getConversionList(startDate, endDate)).thenReturn(exchangeDtoList);

        mockMvc.perform(get("/v1/conversion")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("123")))
                .andExpect(jsonPath("$[0].base", is("USD")))
                .andExpect(jsonPath("$[0].amount", is(100.0)))
                .andExpect(jsonPath("$[0].date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$[0].rates.EUR", is(0.85)))
                .andExpect(jsonPath("$[1].id", is("456")))
                .andExpect(jsonPath("$[1].base", is("EUR")))
                .andExpect(jsonPath("$[1].amount", is(200.0)))
                .andExpect(jsonPath("$[1].date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$[1].rates.USD", is(1.2)));

    }
}