package com.exchangeapi.currencyexchange.dto;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class RateDto {
    private EnumCurrency base;
    private LocalDateTime date;
    List<RateInfoDto> rates;
}
