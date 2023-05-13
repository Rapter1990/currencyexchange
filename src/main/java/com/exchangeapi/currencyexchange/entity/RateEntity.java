package com.exchangeapi.currencyexchange.entity;

import com.exchangeapi.currencyexchange.entity.enums.EnumCurrency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RateEntity")
@SuperBuilder
public class RateEntity extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private EnumCurrency base;

    @ManyToMany(mappedBy = "rates")
    private Set<ExchangeEntity> exchanges;

}
