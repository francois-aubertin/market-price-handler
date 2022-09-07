package com.example.service;

import com.example.domain.CurrencyPair;
import com.example.domain.FxPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {

    private static final CurrencyPair GBP_USD = new CurrencyPair("GBP", "USD");

    @Mock
    private MarkupService markupServiceMock;

    private PriceServiceImpl priceService;

    @BeforeEach
    void init() {
        this.priceService = new PriceServiceImpl(markupServiceMock);
    }

    @Test
    public void handlePriceStoresAPriceWhenItHasNoPreviousPrice() {
        var fxPrice = createGbpUsdPriceWithTimestamp(LocalDateTime.parse("2022-09-07T00:00:00"));
        setupNoOpMarkup();

        priceService.handlePrice(fxPrice);

        ConcurrentMap<CurrencyPair, FxPrice> latestPrices = getLatestPricesMap();
        assertEquals(latestPrices.get(GBP_USD), fxPrice);
    }

    @Test
    public void handlePriceStoresAPriceWhenItReceivesANewerPrice() {
        var oldPrice = createGbpUsdPriceWithTimestamp(LocalDateTime.parse("2022-09-07T00:00:00.000"));
        var newPrice = createGbpUsdPriceWithTimestamp(LocalDateTime.parse("2022-09-07T00:00:00.001"));
        setupNoOpMarkup();

        ConcurrentMap<CurrencyPair, FxPrice> latestPrices = getLatestPricesMap();
        latestPrices.put(GBP_USD, oldPrice);

        priceService.handlePrice(newPrice);

        assertEquals(latestPrices.get(GBP_USD), newPrice);
    }

    @Test
    public void handlePriceDoesNotOverrideAPriceWithAnOlderPrice() {
        var oldPrice = createGbpUsdPriceWithTimestamp(LocalDateTime.parse("2022-09-07T00:00:00.000"));
        var newPrice = createGbpUsdPriceWithTimestamp(LocalDateTime.parse("2022-09-07T00:00:00.001"));
        setupNoOpMarkup();

        ConcurrentMap<CurrencyPair, FxPrice> latestPrices = getLatestPricesMap();
        latestPrices.put(GBP_USD, newPrice);

        priceService.handlePrice(oldPrice);

        assertEquals(latestPrices.get(GBP_USD), newPrice);
    }

    @Test
    public void getLatestPriceReturnsTheLatestPrice() {
        var fxPrice = createGbpUsdPriceWithTimestamp(LocalDateTime.parse("2022-09-07T00:00:00"));
        getLatestPricesMap().put(GBP_USD, fxPrice);

        var actualPrice = priceService.getLatestPrice(GBP_USD);

        assertEquals(fxPrice, actualPrice);
    }

    private void setupNoOpMarkup() {
        Mockito.doAnswer(invocation -> invocation.getArguments()[0]).when(markupServiceMock).addMarkup(any());
    }

    private ConcurrentMap<CurrencyPair, FxPrice> getLatestPricesMap() {
        return (ConcurrentMap<CurrencyPair, FxPrice>) ReflectionTestUtils.getField(priceService, "latestPrices");
    }

    private FxPrice createGbpUsdPriceWithTimestamp(LocalDateTime timestamp) {
        return FxPrice.builder()
                .id(UUID.randomUUID().toString())
                .currencyPair(GBP_USD)
                .bid(new BigDecimal("1.0"))
                .ask(new BigDecimal("2.0"))
                .timestamp(timestamp)
                .build();
    }

}
