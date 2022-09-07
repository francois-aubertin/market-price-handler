package com.example.service;

import com.example.domain.CurrencyPair;
import com.example.domain.FxPrice;
import com.example.messaging.PriceSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService, PriceSubscriber {

    private final ConcurrentMap<CurrencyPair, FxPrice> latestPrices = new ConcurrentHashMap<>();
    private final MarkupService markupService;

    @Override
    public void handlePrice(FxPrice price) {
        var priceWithMarkup = markupService.addMarkup(price);
        latestPrices.compute(price.getCurrencyPair(), (currencyPair, priceInCache) -> {
            if (priceInCache == null || priceInCache.isOlderThan(priceWithMarkup)) {
                return priceWithMarkup;
            } else {
                return priceInCache;
            }
        });
    }

    @Override
    public FxPrice getLatestPrice(CurrencyPair currencyPair) {
        return latestPrices.get(currencyPair);
    }

}
