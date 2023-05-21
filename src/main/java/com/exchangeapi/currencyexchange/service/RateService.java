package com.exchangeapi.currencyexchange.service;

import com.exchangeapi.currencyexchange.dto.RateDto;
import com.exchangeapi.currencyexchange.dto.RateInfoDto;
import com.exchangeapi.currencyexchange.entity.RateEntity;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.payload.response.RateResponse;
import com.exchangeapi.currencyexchange.repository.RateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.exchangeapi.currencyexchange.constants.Constants.EXCHANGE_API_API_KEY;
import static com.exchangeapi.currencyexchange.constants.Constants.EXCHANGE_API_BASE_URL;

@Service
@AllArgsConstructor
@Slf4j
public class RateService {

    private final RateRepository rateRepository;
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public RateDto calculateRate() {
        log.info("ExchangeService | calculateRates() is called");
        return calculateRate(null);
    }

    public RateDto calculateRate(EnumCurrency base) {
        log.info("ExchangeService | calculateRates(EnumCurrency base) is called");
        return calculateRate(base, null);
    }

    public RateDto calculateRate(EnumCurrency base, List<EnumCurrency> targets) {
        log.info("ExchangeService | calculateRates(EnumCurrency base, List<EnumCurrency> targets) is called");
        return calculateRate(base, targets, null);
    }

    public RateDto calculateRate(EnumCurrency base, List<EnumCurrency> targets, LocalDate date) {
        log.info("ExchangeService | calculateRates is called");

        EnumCurrency baseCurrency = Optional.ofNullable(base).orElse(EnumCurrency.EUR);
        List<EnumCurrency> targetsCurrency = Optional.ofNullable(targets)
                .orElseGet(() -> Arrays.asList(EnumCurrency.values()));

        LocalDate rateDate = Optional.ofNullable(date).orElse(LocalDate.now());

        RateEntity rateEntity = rateRepository.findOneByDate(rateDate)
                .orElseGet(() -> saveRatesFromApi(rateDate, base, targets));

        Map<EnumCurrency, Double> rates = rateEntity.getRates();

        BigDecimal baseCurrencyRate = BigDecimal.valueOf(rates.get(baseCurrency));

        List<RateInfoDto> rateList = rates.entrySet()
                .stream()
                .filter(entry -> targetsCurrency.contains(entry.getKey()))
                .map(entryToRate(baseCurrencyRate))
                .toList();

        return RateDto.builder()
                .base(base)
                .rates(rateList)
                .date(rateDate)
                .build();
    }

    private Function<Map.Entry<EnumCurrency, Double>, RateInfoDto> entryToRate(BigDecimal baseCurrencyRate) {
        return entry -> new RateInfoDto(
                entry.getKey(),
                BigDecimal.valueOf(entry.getValue()).divide(baseCurrencyRate, 5, RoundingMode.CEILING).doubleValue()
        );
    }

    private RateEntity saveRatesFromApi(LocalDate rateDate, EnumCurrency base, List<EnumCurrency> targets) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", EXCHANGE_API_API_KEY);
        String url = getExchangeUrl(rateDate, base, targets);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class, headers);
        try {
            RateResponse rates = objectMapper.readValue(response.getBody(), RateResponse.class);
            RateEntity entity = convert(rates);
            entity.setDate(rateDate);
            return rateRepository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getExchangeUrl(LocalDate rateDate, EnumCurrency base, List<EnumCurrency> targets) {
        String symbols = String.join("%2C", targets.stream().map(EnumCurrency::name).toArray(String[]::new));
        return EXCHANGE_API_BASE_URL + rateDate + "?symbols=" + symbols + "&base=" + base;
    }

    private RateEntity convert(RateResponse source) {

        return RateEntity.builder()
                .base(source.base())
                .date(source.date())
                .build();

    }
}
