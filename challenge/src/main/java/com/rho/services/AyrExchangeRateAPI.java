package com.rho.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.rho.model.Exchange;
import com.rho.model.ExchangeDB;
import com.rho.model.enums.APIType;
import com.rho.model.enums.Keys;

//https://www.exchangerate-api.com/docs/standard-requests

@Service
public class AyrExchangeRateAPI implements APIInterface {

    private final String apiKey = "0f5de615a9b5ae5e11c27252";
    private final String baseUrl = "https://v6.exchangerate-api.com/v6";

    public AyrExchangeRateAPI() {}

    @Override
    public JSONObject getExchangeRates(ExchangeDB db, String currency, String targets) {
        // Check for format of Strings
		if (!Check.isCurrencyCode(currency)) {
			return new JSONObject(Check.formatWrongCurrencyFormatAnswer());
		}
		for (String t : targets.replaceAll(" ", "").split(targets)) {
			if (!Check.isCurrencyCode(t)) {
				return new JSONObject(Check.formatWrongCurrencyFormatAnswer());
			}
		}

        Map<String, APIType> apiMap = new HashMap<>();
		Map<String, String> dateMap = new HashMap<>();
		Map<String, Double> ratesMap = new HashMap<>();
		Map<Keys, Object> finalMap = new HashMap<>();

		// Reduce Calls Mechanism
		List<Exchange> oldExchanges = db.checkExchangeUpdateState(currency, targets);
		for (Exchange e : oldExchanges) { // If list size is 0, loop is not executed.
			String to = e.getTo();

			apiMap.put(to, e.getSource());
			dateMap.put(to, e.getDate());
			ratesMap.put(to, e.getRate());
		}

        // Add standard data to final map
        finalMap.put(Keys.CURRENCY_FROM, currency);
		finalMap.put(Keys.CURRENCY_TO, targets);
        finalMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());

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

            if (((String) obj.get("result")).equals("success")) {
                finalMap.put(Keys.SUCCESS, true);
            }
            else {
                finalMap.put(Keys.SUCCESS, false);
            }

            finalMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
            finalMap.put(Keys.RATES, currencyMap);
            finalMap.put(Keys.CALL_EXECUTED, true);
            finalMap.put(Keys.API, APIType.AYR);

            // Store the new fetched data.
            db.saveData(finalMap);
        }
        else {
            // Add old data to final map
            finalMap.put(Keys.SUCCESS, true);
            finalMap.put(Keys.RESULT_DATE, dateMap);
            finalMap.put(Keys.RATES, ratesMap);
            finalMap.put(Keys.CALL_EXECUTED, false);
            finalMap.put(Keys.API, apiMap);
        }

		return new JSONObject(finalMap);
    }

    @Override
    public JSONObject getAllExchangeRates(ExchangeDB db, String currency) {
        // Check for format of Strings
		if (!Check.isCurrencyCode(currency)) {
			return new JSONObject(Check.formatWrongCurrencyFormatAnswer());
		}

        String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);		
		JSONObject obj = Connections.requestAPICall(urlStr);

		Map<Keys, Object> finalMap = new HashMap<>();

        if (((String) obj.get("result")).equals("success")) {
            finalMap.put(Keys.SUCCESS, true);
        }
        else {
            finalMap.put(Keys.SUCCESS, false);
        }
        
        finalMap.put(Keys.CURRENCY_FROM, obj.get("base_code"));
        finalMap.put(Keys.RATES, obj.get("conversion_rates"));
        finalMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
		finalMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
        finalMap.put(Keys.CALL_EXECUTED, true);
		finalMap.put(Keys.API, APIType.AYR);

        // Store the new fetched data.
        db.saveData(finalMap);

		return new JSONObject(finalMap);
    }

    @Override
    public JSONObject convertCurrency(ExchangeDB db, String from, String to, double amount) {
        // Check for format of Strings
        if (!Check.isCurrencyCode(from)) {
            return new JSONObject(Check.formatWrongCurrencyFormatAnswer());
        }
        if (!Check.isCurrencyCode(to)) {
            return new JSONObject(Check.formatWrongCurrencyFormatAnswer());
        }

        Map<Keys, Object> finalMap = new HashMap<>();

        // Reduce Calls Mechanism
		Exchange oldExchange = db.findExchangeRate(from, to);

        // Add standard data to final map
        finalMap.put(Keys.CURRENCY_FROM, from);
        finalMap.put(Keys.CURRENCY_TO, to);
        finalMap.put(Keys.ORIGINAL_AMOUNT, amount);
		Map<String, Integer> time = Connections.getCurrentTime();
        finalMap.put(Keys.REQUEST_TIME, time);

        // Check if there is an old exchange updated
        if (oldExchange != null && !oldExchange.getOutdated()) {
			// Add old data to final map
			double rate = oldExchange.getRate();

            finalMap.put(Keys.SUCCESS, true);
			finalMap.put(Keys.RESULT, rate*amount);
			finalMap.put(Keys.RATES, rate);
			finalMap.put(Keys.RESULT_DATE, oldExchange.getDate());
			finalMap.put(Keys.CALL_EXECUTED, false);
			finalMap.put(Keys.API, oldExchange.getSource());
        }
        else {
			// Request new data from API
            String urlStr = String.format("%s/%s/pair/%s/%s/%d", baseUrl, apiKey, from, to, amount);		
            JSONObject obj = Connections.requestAPICall(urlStr);

            if (((String) obj.get("result")).equals("success")) {
                finalMap.put(Keys.SUCCESS, true);
            }
            else {
                finalMap.put(Keys.SUCCESS, false);
            }

            finalMap.put(Keys.RESULT, obj.get("conversion_result"));
            finalMap.put(Keys.RATES, obj.get("conversion_rate"));
            finalMap.put(Keys.RESULT_DATE, obj.get("time_last_update_utc"));
            finalMap.put(Keys.CALL_EXECUTED, true);
            finalMap.put(Keys.API, APIType.AYR);

            // Store the new fetched data.
            db.saveConversionData(from, to, amount, time, to, APIType.AYR);
        }

        return new JSONObject(finalMap);
    }
}