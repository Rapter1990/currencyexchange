package com.exchangeapi.currencyexchange.config;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.exchangeapi.currencyexchange.constants.Constants.EXCHANGE_CACHE_NAME;

@Component
public class SpringCacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(List.of(EXCHANGE_CACHE_NAME));
        cacheManager.setAllowNullValues(false);
    }
}
