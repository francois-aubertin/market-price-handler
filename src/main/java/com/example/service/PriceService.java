package com.example.service;

import com.example.domain.CurrencyPair;
import com.example.domain.FxPrice;

public interface PriceService {

    FxPrice getLatestPrice(CurrencyPair currencyPair);

}
