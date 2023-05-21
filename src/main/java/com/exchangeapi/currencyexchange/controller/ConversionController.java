package com.exchangeapi.currencyexchange.controller;

import com.exchangeapi.currencyexchange.dto.ExchangeDto;
import com.exchangeapi.currencyexchange.service.ExchangeService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/conversion")
@AllArgsConstructor
public class ConversionController {

    private final ExchangeService exchangeService;

    @RateLimiter(name = "basic")
    @GetMapping("/{id}")
    public ResponseEntity<ExchangeDto> getConversion(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(exchangeService.getConversion(id));
    }

    @RateLimiter(name = "basic")
    @GetMapping
    public ResponseEntity<List<ExchangeDto>> getConversionList(@RequestParam(name = "startDate")  LocalDate startDate,
                                                               @RequestParam(name = "endDate") LocalDate endDate) {

        return ResponseEntity.ok(exchangeService.getConversionList(startDate,
                endDate));
    }
}
