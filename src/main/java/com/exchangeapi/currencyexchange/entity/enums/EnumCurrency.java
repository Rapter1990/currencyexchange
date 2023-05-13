package com.exchangeapi.currencyexchange.entity.enums;

public enum EnumCurrency {
    USD("United States Dollar"),
    EUR("Euro");

    private final String description;

    EnumCurrency(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
