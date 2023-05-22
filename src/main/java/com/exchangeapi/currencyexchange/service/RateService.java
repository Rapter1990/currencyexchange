package com.exchangeapi.currencyexchange.service;

import com.exchangeapi.currencyexchange.dto.RateDto;
import com.exchangeapi.currencyexchange.dto.RateInfoDto;
import com.exchangeapi.currencyexchange.entity.RateEntity;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.payload.response.RateResponse;
import com.exchangeapi.currencyexchange.repository.RateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.exchangeapi.currencyexchange.constants.Constants.EXCHANGE_API_API_KEY;
import static com.exchangeapi.currencyexchange.constants.Constants.EXCHANGE_API_BASE_URL;

@Service
@AllArgsConstructor
@Slf4j
public class RateService {

    private final RateRepository rateRepository;
    private final RestTemplate restTemplate;

    public RateDto calculateRate() {
        log.info("ExchangeService | calculateRates() is called | base null");
        return calculateRate(null);
    }

    public RateDto calculateRate(EnumCurrency base) {
        log.info("ExchangeService | calculateRates(EnumCurrency base) is called | target null");
        return calculateRate(base, null);
    }

    public RateDto calculateRate(EnumCurrency base, List<EnumCurrency> targets) {
        log.info("ExchangeService | calculateRates(EnumCurrency base, List<EnumCurrency> targets) is called  | date null");
        return calculateRate(base, targets, null);
    }

    public RateDto calculateRate(EnumCurrency base, List<EnumCurrency> targets, LocalDate date) {
        log.info("ExchangeService | calculateRates is called");

        base = Optional.ofNullable(base).orElse(EnumCurrency.EUR);
        targets = Optional.ofNullable(targets)
                .orElseGet(() -> Arrays.asList(EnumCurrency.values()));
        date = Optional.ofNullable(date).orElse(LocalDate.now());

        LocalDate finalDate = date;
        EnumCurrency finalBase = base;
        List<EnumCurrency> finalTargets = targets;

        RateEntity rateEntity = rateRepository.findOneByDate(date)
                .orElseGet(() -> saveRatesFromApi(finalDate, finalBase, finalTargets));

        Map<EnumCurrency, Double> rates = rateEntity.getRates();

        List<RateInfoDto> rateInfoList = targets.stream()
                .map(currency -> new RateInfoDto(currency, rates.get(currency)))
                .collect(Collectors.toList());

        return RateDto.builder()
                .id(rateEntity.getId())
                .base(rateEntity.getBase())
                .date(rateEntity.getDate())
                .rates(rateInfoList)
                .build();
    }

    private RateEntity saveRatesFromApi(LocalDate rateDate, EnumCurrency base, List<EnumCurrency> targets) {

        log.info("ExchangeService | saveRatesFromApi is called");

        HttpHeaders headers = new HttpHeaders();
        headers.add("apikey", EXCHANGE_API_API_KEY);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> headersEntity = new HttpEntity<>(headers);
        String url = getExchangeUrl(rateDate, base, targets);

        ResponseEntity<RateResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, headersEntity, RateResponse.class);

        RateResponse rates = responseEntity.getBody();
        RateEntity entity = convert(rates);
        return rateRepository.save(entity);
    }

    private String getExchangeUrl(LocalDate rateDate, EnumCurrency base, List<EnumCurrency> targets) {
        log.info("ExchangeService | getExchangeUrl is called");

        String symbols = String.join("%2C", targets.stream().map(EnumCurrency::name).toArray(String[]::new));
        return EXCHANGE_API_BASE_URL + rateDate + "?symbols=" + symbols + "&base=" + base;
    }

    private RateEntity convert(RateResponse source) {
        log.info("ExchangeService | convert is called");

        Map<EnumCurrency, Double> rates = source.rates();

        return RateEntity.builder()
                .base(source.base())
                .date(source.date())
                .rates(rates)
                .build();

    }
}
