package com.example.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class FxPrice {

    private final String id;
    private final CurrencyPair currencyPair;
    private final BigDecimal bid;
    private final BigDecimal ask;
    private final LocalDateTime timestamp;

    public boolean isOlderThan(FxPrice otherPrice) {
        return timestamp.isBefore(otherPrice.getTimestamp());
    }

}
