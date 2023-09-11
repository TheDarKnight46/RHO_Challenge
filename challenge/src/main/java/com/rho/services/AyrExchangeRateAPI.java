package com.rho.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.rho.model.Keys;

//https://www.exchangerate-api.com/docs/standard-requests

@Service
public class AyrExchangeRateAPI implements APIInterface {

    private final String apiKey = "0f5de615a9b5ae5e11c27252";
    private final String baseUrl = "https://v6.exchangerate-api.com/v6";

    @Override
    public JSONObject getExchangeRates(String currency, String targets) {
        String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);
        JSONObject obj = Connections.requestAPICall(urlStr);

        Map<Keys, Object> valueMap = new HashMap<>();
        Map<String, Object> currencyMap = new HashMap<>();

        // Filter the target from answer
        if (!targets.contains(",")) { // Verification
            return null;            
        }
        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.split(",")));
        JSONObject rates = (JSONObject) obj.get("conversion_rates");

        for (String symbol : symbols) {
            currencyMap.put(symbol, rates.get(symbol));
        }

        // Add values to newly formated map
        if (((String) obj.get("result")).equals("success"))
            valueMap.put(Keys.SUCCESS, "true");
        else
            valueMap.put(Keys.SUCCESS, "false");

        valueMap.put(Keys.CURRENCY_FROM, obj.get("base_code"));
        valueMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
        valueMap.put(Keys.RATES, currencyMap);
        valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
		valueMap.put(Keys.API, "Ayr");

		return new JSONObject(valueMap);
    }

    
    @Override
    public JSONObject getAllExchangeRates(String currency) {
        String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);		
		JSONObject obj = Connections.requestAPICall(urlStr);

		Map<Keys, Object> valueMap = new HashMap<>();

        if (((String) obj.get("result")).equals("success"))
            valueMap.put(Keys.SUCCESS, "true");
        else
            valueMap.put(Keys.SUCCESS, "false");
        
        valueMap.put(Keys.CURRENCY_FROM, obj.get("base_code"));
        valueMap.put(Keys.RATES, obj.get("conversion_rates"));
        valueMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
		valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
		valueMap.put(Keys.API, "Ayr");

		return new JSONObject(valueMap);
    }

    @Override
    public JSONObject convertCurrency(String from, String to, int amount) {
        String urlStr = String.format("%s/%s/pair/%s/%s/%d", baseUrl, apiKey, from, to, amount);		
        JSONObject obj = Connections.requestAPICall(urlStr);

        Map<Keys, Object> valueMap = new HashMap<>();

        if (((String) obj.get("result")).equals("success"))
            valueMap.put(Keys.SUCCESS, "true");
        else
            valueMap.put(Keys.SUCCESS, "false");

        valueMap.put(Keys.CURRENCY_FROM, from);
        valueMap.put(Keys.CURRENCY_TO, to);
        valueMap.put(Keys.ORIGINAL_AMOUNT, amount);
        valueMap.put(Keys.RESULT, obj.get("conversion_result"));
        valueMap.put(Keys.RATES, obj.get("conversion_rate"));
        valueMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
		valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
		valueMap.put(Keys.API, "Ayr");

        return new JSONObject(valueMap);
    }
}