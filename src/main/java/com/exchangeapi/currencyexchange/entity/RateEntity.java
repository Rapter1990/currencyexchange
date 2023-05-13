package com.exchangeapi.currencyexchange.entity;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@Table(name = "RateEntity")
public class RateEntity extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private EnumCurrency base;

    @ManyToMany(mappedBy = "rates")
    private Set<ExchangeEntity> exchanges;
}
