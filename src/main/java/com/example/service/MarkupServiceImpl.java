package com.example.service;

import com.example.domain.FxPrice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MarkupServiceImpl implements MarkupService {

    private static final BigDecimal BID_MARKUP = new BigDecimal("0.999");
    private static final BigDecimal ASK_MARKUP = new BigDecimal("1.001");

    @Override
    public FxPrice addMarkup(FxPrice price) {
        return price.toBuilder()
                .bid(calculateMarkup(price.getBid(), BID_MARKUP))
                .ask(calculateMarkup(price.getAsk(), ASK_MARKUP))
                .build();
    }

    private BigDecimal calculateMarkup(BigDecimal price, BigDecimal multiplier) {
        return price.multiply(multiplier).setScale(4, RoundingMode.HALF_UP); // Ignoring that it's 2 for yen
    }

}
