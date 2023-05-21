package com.exchangeapi.currencyexchange.service;

import com.exchangeapi.currencyexchange.dto.RateDto;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class RateServiceTest {

    @Autowired
    private RateService rateService;

    @Test
    void calculateRate() {
        RateDto rateDto = rateService.calculateRate(
                EnumCurrency.AED,
                List.of(
                        EnumCurrency.SLE,
                        EnumCurrency.CVE
                )
        );

        Assertions.assertEquals(rateDto.getBase(), EnumCurrency.AED);
        Assertions.assertTrue(rateDto.getRates().stream()
                .anyMatch(rateInfoDto -> rateInfoDto.currency().equals(EnumCurrency.SLE))
        );
        Assertions.assertTrue(rateDto.getRates().stream()
                .anyMatch(rateInfoDto -> rateInfoDto.currency().equals(EnumCurrency.CVE))
        );
    }
}