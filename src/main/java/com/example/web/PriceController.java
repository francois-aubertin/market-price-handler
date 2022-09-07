package com.example.web;

import com.example.domain.CurrencyPair;
import com.example.domain.FxPrice;
import com.example.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    // Note: an anti-corruption layer could be used here, it would require a "FxPriceDTO" object with a mapping
    // between FxPrice and FxPriceDto
    @GetMapping("/price")
    public FxPrice getPrice(@RequestParam("base") String baseCurrency,
                            @RequestParam("quote") String quoteCurrency) {
        var currencyPair = new CurrencyPair(baseCurrency, quoteCurrency);

        var price = priceService.getLatestPrice(currencyPair);

        if (price == null) {
            throw new ResponseStatusException(
                    NOT_FOUND,
                    String.format("No price found for currency pair %s/%s", baseCurrency, quoteCurrency)
            );
        } else {
            return price;
        }
    }

}
