package com.rho.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.rho.model.APIType;
import com.rho.model.Exchange;
import com.rho.model.ExchangeDB;
import com.rho.model.Keys;

//https://www.exchangerate-api.com/docs/standard-requests

@Service
public class AyrExchangeRateAPI implements APIInterface {

    private final String apiKey = "0f5de615a9b5ae5e11c27252";
    private final String baseUrl = "https://v6.exchangerate-api.com/v6";

    public AyrExchangeRateAPI() {}

    @Override
    public JSONObject getExchangeRates(ExchangeDB db, String currency, String targets) {
        Map<String, APIType> apiMap = new HashMap<>();
		Map<String, String> dateMap = new HashMap<>();
		Map<String, Double> ratesMap = new HashMap<>();
		Map<Keys, Object> valueMap = new HashMap<>();

		// Reduce Calls Mechanism
		List<Exchange> oldExchanges = db.findUpdatedExchangeRate(currency, targets);

		for (Exchange e : oldExchanges) { // If list size is 0, loop is not executed.
			String to = e.getTo();

			apiMap.put(to, e.getSource());
			dateMap.put(to, e.getDate());
			ratesMap.put(to, e.getRate());
		}

        // Add standard data to value map
        valueMap.put(Keys.CURRENCY_FROM, currency);
		valueMap.put(Keys.CURRENCY_TO, targets);
        valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());

        if (oldExchanges.size() < targets.replaceAll(" ", "").split(",").length) {
			// Request new data from API
            String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);
            JSONObject obj = Connections.requestAPICall(urlStr);

            Map<String, Object> currencyMap = new HashMap<>();

            ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.split(",")));
            JSONObject rates = (JSONObject) obj.get("conversion_rates");

            for (String symbol : symbols) {
                currencyMap.put(symbol, rates.get(symbol));
            }

            if (((String) obj.get("result")).equals("success"))
                valueMap.put(Keys.SUCCESS, true);
            else
                valueMap.put(Keys.SUCCESS, false);

            valueMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
            valueMap.put(Keys.RATES, currencyMap);
            valueMap.put(Keys.CALL_EXECUTED, true);
            valueMap.put(Keys.API, APIType.AYR);

            // Store the new fetched data.
            db.saveData(valueMap);
        }
        else {
            // Add old data to value map
            valueMap.put(Keys.SUCCESS, true);
            valueMap.put(Keys.RESULT_DATE, dateMap);
            valueMap.put(Keys.RATES, ratesMap);
            valueMap.put(Keys.CALL_EXECUTED, false);
            valueMap.put(Keys.API, apiMap);
        }

		return new JSONObject(valueMap);
    }

    
    @Override
    public JSONObject getAllExchangeRates(ExchangeDB db, String currency) {
        String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);		
		JSONObject obj = Connections.requestAPICall(urlStr);

		Map<Keys, Object> valueMap = new HashMap<>();

        if (((String) obj.get("result")).equals("success"))
            valueMap.put(Keys.SUCCESS, true);
        else
            valueMap.put(Keys.SUCCESS, false);
        
        valueMap.put(Keys.CURRENCY_FROM, obj.get("base_code"));
        valueMap.put(Keys.RATES, obj.get("conversion_rates"));
        valueMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
		valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
        valueMap.put(Keys.CALL_EXECUTED, true);
		valueMap.put(Keys.API, APIType.AYR);

        // Store the new fetched data.
        db.saveData(valueMap);

		return new JSONObject(valueMap);
    }

    @Override
    public JSONObject convertCurrency(ExchangeDB db, String from, String to, int amount) {
        Map<Keys, Object> valueMap = new HashMap<>();

        // Reduce Calls Mechanism
		Exchange oldExchange = db.findExchangeRate(from, to);

        // Add standard data to value map
        valueMap.put(Keys.CURRENCY_FROM, from);
        valueMap.put(Keys.CURRENCY_TO, to);
        valueMap.put(Keys.ORIGINAL_AMOUNT, amount);
		Map<String, Integer> time = Connections.getCurrentTime();
        valueMap.put(Keys.REQUEST_TIME, time);

        // Check if there is an old exchange updated
        if (oldExchange != null && !oldExchange.getOutdated()) {
			// Add old data to value map
			double rate = oldExchange.getRate();

            valueMap.put(Keys.SUCCESS, true);
			valueMap.put(Keys.RESULT, rate*amount);
			valueMap.put(Keys.RATES, rate);
			valueMap.put(Keys.RESULT_DATE, oldExchange.getDate());
			valueMap.put(Keys.CALL_EXECUTED, false);
			valueMap.put(Keys.API, oldExchange.getSource());
        }
        else {
			// Request new data from API
            String urlStr = String.format("%s/%s/pair/%s/%s/%d", baseUrl, apiKey, from, to, amount);		
            JSONObject obj = Connections.requestAPICall(urlStr);

            if (((String) obj.get("result")).equals("success"))
                valueMap.put(Keys.SUCCESS, true);
            else
                valueMap.put(Keys.SUCCESS, false);

            valueMap.put(Keys.RESULT, obj.get("conversion_result"));
            valueMap.put(Keys.RATES, obj.get("conversion_rate"));
            valueMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
            valueMap.put(Keys.CALL_EXECUTED, true);
            valueMap.put(Keys.API, APIType.AYR);

            // Store the new fetched data.
            db.saveConversionData(from, to, amount, time, to, APIType.AYR);
        }

        return new JSONObject(valueMap);
    }
}