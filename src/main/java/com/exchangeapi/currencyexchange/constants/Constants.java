package com.exchangeapi.currencyexchange.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static String EXCHANGE_API_BASE_URL;
    public static String EXCHANGE_API_API_KEY;

    public static String EXCHANGE_CACHE_NAME;
    public static Integer EXCHANGE_API_CALL_LIMIT;

    @Value("${exchange-api.api-url}")
    public void setExchangeApiBaseUrl(String apiUrl) {
        EXCHANGE_API_BASE_URL = apiUrl;
    }

    @Value("${exchange-api.api-key}")
    public void setExchangeApiKey(String apiKey) {
        EXCHANGE_API_API_KEY = apiKey;
    }

    @Value("${exchange-api.cache-name}")
    public void setExchangeCacheName(String cacheName) {
        EXCHANGE_CACHE_NAME = cacheName;
    }

    @Value("${exchange-api.api-call-limit}")
    public void setExchangeApiCallLimit(Integer apiCallLimit) {
        EXCHANGE_API_CALL_LIMIT = apiCallLimit;
    }

}
