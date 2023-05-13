package com.exchangeapi.currencyexchange.payload.request;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RateRequest {
    EnumCurrency base;
    List<EnumCurrency> target;
    LocalDate date;
}
