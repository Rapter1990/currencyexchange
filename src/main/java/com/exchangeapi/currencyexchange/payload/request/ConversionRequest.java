package com.exchangeapi.currencyexchange.payload.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ConversionRequest {
    LocalDate startDate;
    LocalDate endDate;
}
