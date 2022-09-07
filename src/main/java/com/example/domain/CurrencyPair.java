package com.example.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CurrencyPair {

    public static CurrencyPair parse(String s) {
        if (s.length() != 7) {
            throw new IllegalArgumentException(String.format("Invalid currency pair format, found %s", s));
        }
        return new CurrencyPair(s.substring(0, 3), s.substring(4));
    }

    private final String base;
    private final String quote;

}
