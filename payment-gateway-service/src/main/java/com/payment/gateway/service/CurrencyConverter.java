package com.payment.gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.payment.gateway.enums.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyConverter {

    private final RestTemplate restTemplate;
    @Value("${currency.api.url}")
    private String currencyApiUrl;
    @Value("${currency.api.key}")
    private String apiKey;

    public CurrencyConverter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Converts the given amount from one currency to another using real-time exchange rates.
     *
     * @param amount       The amount to convert.
     * @param fromCurrency The currency code of the original currency.
     * @param toCurrency   The currency code of the target currency.
     * @return The converted amount in the target currency.
     */
    public BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);

        return amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Fetches the exchange rate between two currencies.
     * The result is cached to minimize external API calls.
     *
     * @param fromCurrency The currency code of the original currency.
     * @param toCurrency   The currency code of the target currency.
     * @return The exchange rate from the original currency to the target currency.
     */
    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '_' + #toCurrency")
    public BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency) {
        String url = String.format("%s%s&currencies=%s&base_currency=%s", currencyApiUrl, apiKey, toCurrency, fromCurrency);

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("data")) {
                double rate = response.get("data").get(toCurrency.name()).asDouble();
                return BigDecimal.valueOf(rate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch exchange rate from external service.");
        }

        throw new RuntimeException("Exchange rate not found.");
    }
}
