package com.example.service;

import com.example.domain.FxPrice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MarkupServiceTest {

    private MarkupServiceImpl markupService = new MarkupServiceImpl();

    @Test
    public void applyMarkupForBidAndAsk() {
        var price = FxPrice.builder()
                .bid(new BigDecimal("0.5"))
                .ask(new BigDecimal("1"))
                .build();

        var priceWithMarkup = markupService.addMarkup(price);

        Assertions.assertEquals(new BigDecimal("0.4995"), priceWithMarkup.getBid());
        Assertions.assertEquals(new BigDecimal("1.0010"), priceWithMarkup.getAsk());
    }
}
