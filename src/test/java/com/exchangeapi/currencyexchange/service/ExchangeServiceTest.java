package com.exchangeapi.currencyexchange.service;

import com.exchangeapi.currencyexchange.base.BaseServiceTest;
import com.exchangeapi.currencyexchange.dto.ExchangeDto;
import com.exchangeapi.currencyexchange.dto.RateDto;
import com.exchangeapi.currencyexchange.dto.RateInfoDto;
import com.exchangeapi.currencyexchange.entity.ExchangeEntity;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.repository.ExchangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ExchangeServiceTest extends BaseServiceTest {

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private RateService rateService;

    @InjectMocks
    private ExchangeService exchangeService;

    @Test
    public void testCalculateExchangeRate() {
        // Mocking dependencies
        EnumCurrency baseCurrency = EnumCurrency.USD;
        List<EnumCurrency> targetCurrencies = Arrays.asList(EnumCurrency.EUR, EnumCurrency.GBP);

        List<RateInfoDto> rateInfoDtos = Arrays.asList(
                new RateInfoDto(EnumCurrency.EUR, 0.85),
                new RateInfoDto(EnumCurrency.GBP, 0.72)
        );

        RateDto rateDto = RateDto.builder()
                .base(baseCurrency)
                .rates(rateInfoDtos)
                .build();

        when(rateService.calculateRate(eq(baseCurrency), eq(targetCurrencies))).thenReturn(rateDto);

        Double amount = 100.0;
        ExchangeEntity savedExchange = ExchangeEntity.builder()
                .base(baseCurrency)
                .amount(amount)
                .rates(rateInfoDtos.stream().collect(Collectors.toMap(RateInfoDto::currency, RateInfoDto::rate)))
                .build();
        when(exchangeRepository.saveAndFlush(any(ExchangeEntity.class))).thenReturn(savedExchange);

        // Call the method
        ExchangeDto result = exchangeService.calculateExchangeRate(amount, baseCurrency, targetCurrencies);

        // Assertions
        assertNotNull(result);
        assertEquals(savedExchange.getId(), result.getId());
        assertEquals(savedExchange.getBase(), result.getBase());
        assertEquals(savedExchange.getAmount(), result.getAmount());
        assertEquals(savedExchange.getDate(), result.getDate());
        assertEquals(savedExchange.getRates(), result.getRates());
    }

    @Test
    public void testGetConversion() {
        String exchangeId = "exampleId";
        EnumCurrency baseCurrency = EnumCurrency.USD;
        Double amount = 100.0;
        List<RateInfoDto> rateInfoDtos = Arrays.asList(
                new RateInfoDto(EnumCurrency.EUR, 0.85),
                new RateInfoDto(EnumCurrency.GBP, 0.72)
        );
        ExchangeEntity savedExchange = ExchangeEntity.builder()
                .base(baseCurrency)
                .amount(amount)
                .rates(rateInfoDtos.stream().collect(Collectors.toMap(RateInfoDto::currency, RateInfoDto::rate)))
                .build();

        when(exchangeRepository.findById(eq(exchangeId))).thenReturn(Optional.of(savedExchange));

        // Call the method
        ExchangeDto result = exchangeService.getConversion(exchangeId);

        // Assertions
        assertNotNull(result);
        assertEquals(savedExchange.getId(), result.getId());
        assertEquals(savedExchange.getBase(), result.getBase());
        assertEquals(savedExchange.getAmount(), result.getAmount());
        assertEquals(savedExchange.getDate(), result.getDate());
        assertEquals(savedExchange.getRates(), result.getRates());
    }

    @Test
    public void testGetConversionList() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        EnumCurrency baseCurrency = EnumCurrency.USD;
        Double amount = 100.0;
        List<RateInfoDto> rateInfoDtos = Arrays.asList(
                new RateInfoDto(EnumCurrency.EUR, 0.85),
                new RateInfoDto(EnumCurrency.GBP, 0.72)
        );
        ExchangeEntity exchangeEntity = ExchangeEntity.builder()
                .base(baseCurrency)
                .amount(amount)
                .rates(rateInfoDtos.stream().collect(Collectors.toMap(RateInfoDto::currency, RateInfoDto::rate)))
                .build();

        List<ExchangeEntity> exchangeEntities = Collections.singletonList(exchangeEntity);
        when(exchangeRepository.findByDateBetween(eq(startDate), eq(endDate))).thenReturn(exchangeEntities);

        // Call the method
        List<ExchangeDto> result = exchangeService.getConversionList(startDate, endDate);

        // Assertions
        assertNotNull(result);
        assertEquals(exchangeEntities.size(), result.size());
    }
}