package com.exchangeapi.currencyexchange.exception;

public class RateNotFoundException extends RuntimeException {
    public RateNotFoundException(String message) {
        super(message);
    }
}
