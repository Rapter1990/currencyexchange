package com.exchangeapi.currencyexchange.payload.response;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;

@Builder
public record RateResponse(EnumCurrency base,
                           LocalDate date,
                           Map<EnumCurrency, Double> rates,
                           boolean success,
                           long timestamp) {
}
