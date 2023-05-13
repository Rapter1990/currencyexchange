package com.exchangeapi.currencyexchange.payload.request;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import lombok.Data;

import java.util.List;

@Data
public class ExchangeRequest {
    EnumCurrency base;
    List<EnumCurrency> target;
    Double amount;
}
