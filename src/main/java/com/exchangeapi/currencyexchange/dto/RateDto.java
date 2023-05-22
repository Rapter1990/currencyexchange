package com.exchangeapi.currencyexchange.dto;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class RateDto {
    private String id;
    private EnumCurrency base;
    private LocalDate date;
    List<RateInfoDto> rates;
}
