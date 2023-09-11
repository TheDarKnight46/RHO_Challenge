package com.rho.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

//https://www.exchangerate-api.com/docs/standard-requests

public class AYRTechExchangeRateAPI implements APIInterface {

    private final String apiKey = "0f5de615a9b5ae5e11c27252";
    private final String baseUrl = "https://v6.exchangerate-api.com/v6";

    @Override
    public JSONObject getExchangeRates(String currency, String targets) {
        String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);
        JSONObject obj = Connections.requestAPICall(urlStr);

        Map<String, Object> valueMap = new HashMap<>();
        Map<String, Object> currencyMap = new HashMap<>();

        if (!targets.contains(",")) { //verification
            return null;            
        }
        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.split(",")));

        if (((String) obj.get("result")).equals("success"))
            valueMap.put("Success", "true");
        else
            valueMap.put("Success", "false");

        valueMap.put("Currency", obj.get("base_code"));
        valueMap.put("Date", obj.get("time_last_update_utc"));
        JSONObject rates = (JSONObject) obj.get("conversion_rates");

        for (String symbol : symbols) {
            currencyMap.put(symbol, rates.get(symbol));
        }

        valueMap.put("Rates", new JSONObject(currencyMap));

		return new JSONObject(valueMap);
    }

    
    @Override
    public JSONObject getAllExchangeRates(String currency) {
        String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);		
		JSONObject obj = Connections.requestAPICall(urlStr);

		Map<String, Object> valueMap = new HashMap<>();

        if (((String) obj.get("result")).equals("success"))
            valueMap.put("Success", "true");
        else
            valueMap.put("Success", "false");
        
        valueMap.put("Currency", obj.get("base_code"));
        valueMap.put("Rates", obj.get("conversion_rates"));
        valueMap.put("Date", obj.get("time_last_update_utc"));

		return new JSONObject(valueMap);
    }

    @Override
    public JSONObject convertCurrency(String from, String to, int amount) {
        String urlStr = String.format("%s/%s/pair/%s/%s/%d", baseUrl, apiKey, from, to, amount);		
        JSONObject obj = Connections.requestAPICall(urlStr);

        Map<String, Object> valueMap = new HashMap<>();

        if (((String) obj.get("result")).equals("success"))
            valueMap.put("Success", "true");
        else
            valueMap.put("Success", "false");

        valueMap.put("Currency From", from);
        valueMap.put("Currency To", to);
        valueMap.put("Original Amount", amount);
        valueMap.put("Result", obj.get("conversion_result"));
        valueMap.put("Rates", obj.get("conversion_rate"));
        valueMap.put("Date", obj.get("time_last_update_utc"));

        return new JSONObject(valueMap);
    }
}