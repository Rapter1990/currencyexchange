package com.exchangeapi.currencyexchange.entity;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ExchangeEntity")
@SuperBuilder
public class ExchangeEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private EnumCurrency base;

    private Double amount;

    @ElementCollection
    @CollectionTable(name = "exchange_mapping",
            joinColumns = {@JoinColumn(name = "exchange_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "currency")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "rates")
    private Map<EnumCurrency, Double> rates;
}
