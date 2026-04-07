package com.abdulkhadirjallow.digitalwalletapi.service;

import com.abdulkhadirjallow.digitalwalletapi.enums.Currency;
import com.abdulkhadirjallow.digitalwalletapi.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class FxRateService {

    @Value("${fx.api.key}")
    private String fxApiKey;

    @Value("${fx.api.url}")
    private String fxApiUrl;

    private final RestTemplate restTemplate;

    public FxRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getExchangeRate(Currency from, Currency to) {
        if (from == to) {
            return BigDecimal.ONE;
        }

        String url = fxApiUrl + "/" + fxApiKey + "/latest/" + from.name();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            throw new BadRequestException("Failed to fetch exchange rates");
        }

        Object result = response.get("result");
        if (result == null || !"success".equalsIgnoreCase(result.toString())) {
            throw new BadRequestException("Exchange rate API returned an error");
        }

        Object conversionRatesObj = response.get("conversion_rates");
        if (!(conversionRatesObj instanceof Map<?, ?> conversionRates)) {
            throw new BadRequestException("Invalid exchange rate response");
        }

        Object rateObj = conversionRates.get(to.name());
        if (rateObj == null) {
            throw new BadRequestException("Exchange rate not found for " + to);
        }

        return new BigDecimal(rateObj.toString());
    }
}
