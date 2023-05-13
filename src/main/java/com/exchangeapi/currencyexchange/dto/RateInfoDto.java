package com.exchangeapi.currencyexchange.dto;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;

public record RateInfoDto(EnumCurrency currency, Double rate) {}
