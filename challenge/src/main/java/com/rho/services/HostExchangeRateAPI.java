package com.rho.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.rho.model.APIType;
import com.rho.model.Exchange;
import com.rho.model.ExchangeDB;
import com.rho.model.Keys;

//https://exchangerate.host/#/#docs

@Service
public class HostExchangeRateAPI implements APIInterface {

	private final String baseUrl = "https://api.exchangerate.host/";
	
    
    public HostExchangeRateAPI() {}

	/**
	 * Get latest exchange rates for a specified currency and target
	 * @param currency Currency from which to exchange.
	 * @param target List of target currencies to get the exchange rate.
	 * @param amount Amount of currency to exchange.
	 * @return Result field of JsonObject as string.
	 */
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

		// Check if API call is still required
		if (oldExchanges.size() < targets.replaceAll(" ", "").split(",").length) {
			// Request new data from API
			String urlStr = String.format("%s/latest?base=%s&symbols=%s", baseUrl, currency, targets);
			JSONObject obj = Connections.requestAPICall(urlStr);

			valueMap.put(Keys.SUCCESS, obj.get("success"));
	        valueMap.put(Keys.RATES, obj.get("rates"));
			valueMap.put(Keys.RESULT_DATE, obj.get("date"));
			valueMap.put(Keys.API, APIType.HOST);
			valueMap.put(Keys.CALL_EXECUTED, true);

            // Store the new fetched data.
			db.saveData(valueMap);
		}
		else {
			// Add old data to value map
			valueMap.put(Keys.SUCCESS, true);
			valueMap.put(Keys.RATES, ratesMap);
			valueMap.put(Keys.RESULT_DATE, dateMap);
			valueMap.put(Keys.API, apiMap);
			valueMap.put(Keys.CALL_EXECUTED, false);
		}

		return new JSONObject(valueMap);
	}

	/**
	 * Get all latest exchange rates for a specified currency.
	 * @param currency Currency from which to exchange.
	 * @param amount Amount of the currency to exchange.
	 * @return Result field of JsonObject as string.
	 */
	@Override
	public JSONObject getAllExchangeRates(ExchangeDB db, String currency) {
		String urlStr = String.format("%s/latest?base=%s", baseUrl, currency);
		JSONObject obj = Connections.requestAPICall(urlStr);

		Map<Keys, Object> valueMap = new HashMap<>();

		valueMap.put(Keys.SUCCESS, obj.get("success"));
		valueMap.put(Keys.CURRENCY_FROM, obj.get("base"));
		valueMap.put(Keys.RATES, obj.get("rates"));
		valueMap.put(Keys.RESULT_DATE, obj.get("date"));
		valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
        valueMap.put(Keys.CALL_EXECUTED, true);
		valueMap.put(Keys.API, APIType.HOST);

		// Store the new fetched data.
		db.saveData(valueMap);

		return new JSONObject(valueMap);		
	}

	/**
	 * Convert currency from one to another.
	 * @param from Currency from which to convert.
	 * @param to Currency to convert to.
	 * @return Result field of JsonObject as string.
	 */
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
			String urlStr = String.format("%s/convert?from=%s&to=%s&amount=%d", baseUrl, from, to, amount);
			JSONObject obj = Connections.requestAPICall(urlStr);
			
 			valueMap.put(Keys.SUCCESS, obj.get("success"));
        	valueMap.put(Keys.RESULT, obj.get("result"));
			valueMap.put(Keys.RATES, ((JSONObject) obj.get("info")).get("rate"));
        	valueMap.put(Keys.RESULT_DATE, obj.get("date"));
			valueMap.put(Keys.CALL_EXECUTED, true);
			valueMap.put(Keys.API, APIType.HOST);

			// Store the new fetched data.
			db.saveConversionData(from, to, amount, time, to, APIType.HOST);
		}

		return new JSONObject(valueMap);
	}
}
