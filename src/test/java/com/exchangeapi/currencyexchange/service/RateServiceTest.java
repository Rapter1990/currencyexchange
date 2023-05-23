package com.exchangeapi.currencyexchange.service;

import com.exchangeapi.currencyexchange.base.BaseServiceTest;
import com.exchangeapi.currencyexchange.dto.RateDto;
import com.exchangeapi.currencyexchange.dto.RateInfoDto;
import com.exchangeapi.currencyexchange.entity.RateEntity;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.payload.response.RateResponse;
import com.exchangeapi.currencyexchange.repository.RateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class RateServiceTest extends BaseServiceTest {


    @Mock
    private RateRepository rateRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RateService rateService;


    @Test
    void whenCalculateRate_butRateRepositoryFoundOne() {

        // Mocked data
        EnumCurrency base = EnumCurrency.EUR;
        List<EnumCurrency> targets = Arrays.asList(EnumCurrency.USD, EnumCurrency.GBP);
        LocalDate date = LocalDate.of(2023, 5, 22);

        // Mocked rate entity
        RateEntity mockedRateEntity = new RateEntity();
        mockedRateEntity.setBase(base);
        mockedRateEntity.setDate(date);
        Map<EnumCurrency, Double> rates = new HashMap<>();
        rates.put(EnumCurrency.USD, 1.2);
        rates.put(EnumCurrency.GBP, 0.9);
        mockedRateEntity.setRates(rates);

        List<RateInfoDto> rateInfoList = targets.stream()
                .map(currency -> new RateInfoDto(currency, rates.get(currency)))
                .collect(Collectors.toList());

        RateDto expected = RateDto.builder()
                .id(mockedRateEntity.getId())
                .base(mockedRateEntity.getBase())
                .date(mockedRateEntity.getDate())
                .rates(rateInfoList)
                .build();

        // Mock repository behavior
        when(rateRepository.findOneByDate(date)).thenReturn(Optional.of(mockedRateEntity));


        // Call the method
        RateDto result = rateService.calculateRate(base, targets, date);

        // Verify the result
        assertThat(result.getBase()).isEqualTo(expected.getBase());
        assertThat(result.getDate()).isEqualTo(expected.getDate());
        assertThat(result.getRates()).hasSize(2);
        assertThat(result.getRates()).containsExactlyInAnyOrder(
                new RateInfoDto(EnumCurrency.USD, 1.2),
                new RateInfoDto(EnumCurrency.GBP, 0.9)
        );

        // Verify repository method was called
        verify(rateRepository, times(1)).findOneByDate(date);

        // The saveRatesFromApi method won't be run because the rateRepository.findOneByDate return the mockedRateEntity
        verify(restTemplate, times(0)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RateResponse.class)
        );
    }

    @Test
    void whenCalculateRate_andRateRepositoryNotFound() {
        // Mocked data
        EnumCurrency base = EnumCurrency.EUR;
        List<EnumCurrency> targets = Arrays.asList(EnumCurrency.USD, EnumCurrency.GBP);
        LocalDate date = LocalDate.of(2023, 5, 22);

        // Mocked rate entity
        RateEntity mockedRateEntity = new RateEntity();
        mockedRateEntity.setBase(base);
        mockedRateEntity.setDate(date);
        Map<EnumCurrency, Double> rates = new HashMap<>();
        rates.put(EnumCurrency.USD, 1.2);
        rates.put(EnumCurrency.GBP, 0.9);
        mockedRateEntity.setRates(rates);

        List<RateInfoDto> rateInfoList = targets.stream()
                .map(currency -> new RateInfoDto(currency, rates.get(currency)))
                .collect(Collectors.toList());

        RateDto expected = RateDto.builder()
                .id(mockedRateEntity.getId())
                .base(mockedRateEntity.getBase())
                .date(mockedRateEntity.getDate())
                .rates(rateInfoList)
                .build();

        // Mock API response
        RateResponse mockedRateResponse = RateResponse.builder()
                .base(base)
                .rates(rates)
                .date(date)
                .build();

        ResponseEntity<RateResponse> mockedResponseEntity = ResponseEntity.ok().body(mockedRateResponse);

        // Mock repository behavior
        when(rateRepository.findOneByDate(date)).thenReturn(Optional.empty()); // Return null to simulate repository not finding the entity

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RateResponse.class)
        )).thenReturn(mockedResponseEntity);

        // Mock saveRatesFromApi behavior
        when(rateRepository.save(any(RateEntity.class))).thenReturn(mockedRateEntity);

        // Call the method
        RateDto result = rateService.calculateRate(base, targets, date);

        // Verify the result
        assertThat(result.getBase()).isEqualTo(expected.getBase());
        assertThat(result.getDate()).isEqualTo(expected.getDate());
        assertThat(result.getRates()).hasSize(2);
        assertThat(result.getRates()).containsExactlyInAnyOrder(
                new RateInfoDto(EnumCurrency.USD, 1.2),
                new RateInfoDto(EnumCurrency.GBP, 0.9)
        );

        // Verify repository method was called
        verify(rateRepository, times(1)).findOneByDate(date);
        verify(rateRepository, times(1)).save(any(RateEntity.class));

        // Verify restTemplate.exchange was called
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RateResponse.class)
        );
    }

}