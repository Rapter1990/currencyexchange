package com.exchangeapi.currencyexchange.controller;

import com.exchangeapi.currencyexchange.dto.RateDto;
import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import com.exchangeapi.currencyexchange.service.RateService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/v1/rate")
@AllArgsConstructor
@Slf4j
public class RateController {

    private final RateService rateService;

    @RateLimiter(name = "basic")
    @GetMapping
    public ResponseEntity<RateDto> getRates(@RequestParam(name = "base",required = false) EnumCurrency base,
                            @RequestParam(name = "target",required = false) List<EnumCurrency> target,
                            @RequestParam(name = "date",required = false) @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate date) {
        log.info("RateController | getRates is called");
        return ResponseEntity.ok(rateService.calculateRate(base, target,date));
    }
}
