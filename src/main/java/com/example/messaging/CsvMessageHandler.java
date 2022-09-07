package com.example.messaging;

import com.example.domain.CurrencyPair;
import com.example.domain.FxPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvMessageHandler {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS");

    private final List<PriceSubscriber> subscribers;

    public void processCsv(String csv) {
        try (var scanner = new Scanner(csv)) {
            while (scanner.hasNextLine()) {
                processEachLineWithErrorHandling(scanner.nextLine());
            }
        }
    }

    private void processEachLineWithErrorHandling(String line) {
        try {
            String[] items = Arrays.stream(line.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
            if (items.length < 5) {
                log.error("Received CSV line with less than 5 items: {}", line);
            } else {
                var price = FxPrice.builder()
                        .id(items[0])
                        .currencyPair(CurrencyPair.parse(items[1]))
                        .bid(new BigDecimal(items[2]))
                        .ask(new BigDecimal(items[3]))
                        .timestamp(LocalDateTime.parse(items[4], DATE_TIME_FORMATTER))
                        .build();
                notifySubscribersWithErrorHandling(price);
            }
        } catch (Exception e) {
            log.error("Error while processing CSV line", e);
        }
    }

    private void notifySubscribersWithErrorHandling(FxPrice price) {
        for (PriceSubscriber subscriber : subscribers) {
            try {
                subscriber.handlePrice(price);
            } catch (Exception e) {
                log.error("Error in price subscriber", e);
            }
        }
    }

}
