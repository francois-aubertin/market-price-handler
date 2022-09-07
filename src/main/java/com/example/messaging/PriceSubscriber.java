package com.example.messaging;

import com.example.domain.FxPrice;

public interface PriceSubscriber {

    void handlePrice(FxPrice price);

}
