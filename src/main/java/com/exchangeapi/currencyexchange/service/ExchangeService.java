package com.exchangeapi.currencyexchange.service;

import com.exchangeapi.currencyexchange.dto.ExchangeDto;
import com.exchangeapi.currencyexchange.dto.RateDto;
import com.exchangeapi.currencyexchange.dto.RateInfoDto;
import com.exchangeapi.currencyexchange.entity.ExchangeEntity;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.exception.ExchangeNotFoundException;
import com.exchangeapi.currencyexchange.repository.ExchangeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@CacheConfig(cacheNames = {"exchanges"})
@AllArgsConstructor
@Slf4j
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final RateService rateService;


    @CachePut(key = "#result.id")
    public ExchangeDto calculateExchangeRate(Double amount, EnumCurrency base, List<EnumCurrency> targets) {
        log.info("ExchangeService | calculateExchangeRate is called");

        RateDto rateDto = rateService.calculateRate(base,targets);

        Map<EnumCurrency, Double> exchanges = rateDto.getRates()
                .stream()
                .map(rateInfoDto -> mapToRateInfoDto(rateInfoDto,amount))
                .collect(Collectors.toMap(RateInfoDto::currency,RateInfoDto::rate));

        ExchangeEntity exchangeEntity = ExchangeEntity.builder()
                .base(rateDto.getBase())
                .rates(exchanges)
                .amount(amount)
                .date(LocalDate.now())
                .build();

        ExchangeEntity savedExchange = exchangeRepository.saveAndFlush(exchangeEntity);

        return mapToExchangeDTO(savedExchange);
    }


    @Cacheable(key = "#id")
    public ExchangeDto getConversion(String id) {
        log.info("ExchangeService | calculateExchangeRate is called");
        return exchangeRepository.findById(id).map(this::mapToExchangeDTO).orElseThrow(() -> new ExchangeNotFoundException("Not Found"));
    }

    public List<ExchangeDto> getConversionList(LocalDate startDate, LocalDate endDate) {
        log.info("ExchangeService | getConversionList is called");
        return exchangeRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::mapToExchangeDTO)
                .collect(Collectors.toList());
    }

    private ExchangeDto mapToExchangeDTO(ExchangeEntity exchangeEntity) {
        log.info("ExchangeService | mapToExchangeDTO is called");
        return ExchangeDto.builder()
                .id(exchangeEntity.getId())
                .amount(exchangeEntity.getAmount())
                .base(exchangeEntity.getBase())
                .date(exchangeEntity.getDate())
                .rates(exchangeEntity.getRates())
                .build();

    }

    private RateInfoDto mapToRateInfoDto(RateInfoDto rateInfoDto, Double amount) {
        log.info("ExchangeService | mapToRateInfoDto is called");
        EnumCurrency enumCurrency = rateInfoDto.currency();
        Double rate = BigDecimal.valueOf(rateInfoDto.rate()).multiply(BigDecimal.valueOf(amount),
                new MathContext(5, RoundingMode.CEILING)).doubleValue();
        return new RateInfoDto(enumCurrency,rate);
    }

    @CacheEvict(allEntries = true)
    @PostConstruct
    @Scheduled(fixedRateString = "${exchange-api.cache-ttl}")
    public void clearCache(){
        log.info("Caches are cleared");
    }
}
