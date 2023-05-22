package com.exchangeapi.currencyexchange.service;

import com.exchangeapi.currencyexchange.base.BaseServiceTest;
import com.exchangeapi.currencyexchange.dto.RateDto;
import com.exchangeapi.currencyexchange.dto.RateInfoDto;
import com.exchangeapi.currencyexchange.entity.RateEntity;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.payload.response.RateResponse;
import com.exchangeapi.currencyexchange.repository.RateRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static com.exchangeapi.currencyexchange.constants.Constants.EXCHANGE_API_API_KEY;
import static com.exchangeapi.currencyexchange.constants.Constants.EXCHANGE_API_BASE_URL;
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
    void testCalculateRate() {

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

        // Mock repository behavior
        when(rateRepository.findOneByDate(date)).thenReturn(Optional.of(mockedRateEntity));

        // Mock API response
        RateResponse mockedRateResponse = RateResponse.builder()
                .base(base)
                .rates(rates)
                .date(date)
                .build();

        // Create a mock response entity with the expected headers and body
        ResponseEntity<RateResponse> mockedResponseEntity = ResponseEntity.ok()
                .body(mockedRateResponse);

        // Mock RestTemplate behavior
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RateResponse.class)
        )).thenReturn(mockedResponseEntity);

        // Call the method
        RateDto result = rateService.calculateRate(base, targets, date);


        // Verify API call was made
        String expectedUrl = getExchangeUrl(date, base, targets);
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("apikey", EXCHANGE_API_API_KEY);
        HttpEntity<String> expectedHttpEntity = new HttpEntity<>(expectedHeaders);


        // Verify the result
        assertThat(result.getBase()).isEqualTo(base);
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getRates()).hasSize(2);
        assertThat(result.getRates()).containsExactlyInAnyOrder(
                new RateInfoDto(EnumCurrency.USD, 1.2),
                new RateInfoDto(EnumCurrency.GBP, 0.9)
        );

        // Verify repository method was called
        verify(rateRepository, times(1)).findOneByDate(date);

        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                eq(expectedHttpEntity),
                eq(RateResponse.class)
        );
    }

    private String getExchangeUrl(LocalDate rateDate, EnumCurrency base, List<EnumCurrency> targets) {

        String symbols = String.join("%2C", targets.stream().map(EnumCurrency::name).toArray(String[]::new));
        return EXCHANGE_API_BASE_URL + rateDate + "?symbols=" + symbols + "&base=" + base;
    }
}