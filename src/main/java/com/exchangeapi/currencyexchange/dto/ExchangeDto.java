package com.exchangeapi.currencyexchange.dto;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Builder
@Getter
public class ExchangeDto {
    private String id;
    private EnumCurrency base;
    private Double amount;
    private LocalDate date;
    private Map<EnumCurrency, Double> rates;
}
