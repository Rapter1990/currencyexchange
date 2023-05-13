package com.exchangeapi.currencyexchange.entity;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "ExchangeEntity")
public class ExchangeEntity extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private EnumCurrency base;

    private Double amount;

    @ManyToMany
    @JoinTable(name = "exchange_rate_mapping",
            joinColumns = @JoinColumn(name = "exchange_id"),
            inverseJoinColumns = @JoinColumn(name = "rate_id"))
    private Set<RateEntity> rates;
}
