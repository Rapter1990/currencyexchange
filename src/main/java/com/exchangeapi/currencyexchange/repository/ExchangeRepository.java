package com.exchangeapi.currencyexchange.repository;

import com.exchangeapi.currencyexchange.entity.ExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRepository extends JpaRepository<ExchangeEntity, String> {

    List<ExchangeEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
