package com.rho.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.rho.model.Exchange;
import com.rho.model.ExchangeDB;
import com.rho.model.enums.APIType;
import com.rho.model.enums.Keys;

//https://exchangerate.host/#/#docs

@Service
public class HostExchangeRateAPI implements APIInterface {

	private final String baseUrl = "https://api.exchangerate.host/";
	
    public HostExchangeRateAPI() {}

	/**
	 * Get latest exchange rates for a specified currency and target.
	 * @param db Database of class ExchangeDB storing all the exchange rates.
	 * @param currency Currency from which to exchange.
	 * @param target List of target currencies to get the exchange rate.
	 * @return Result field of JsonObject as string.
	 */
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

		// Check if API call is still required
		if (oldExchanges.size() < targets.replaceAll(" ", "").split(",").length) {
			// Request new data from API
			String urlStr = String.format("%s/latest?base=%s&symbols=%s", baseUrl, currency, targets);
			JSONObject obj = Connections.requestAPICall(urlStr);

			finalMap.put(Keys.SUCCESS, obj.get("success"));
	        finalMap.put(Keys.RATES, obj.get("rates"));
			finalMap.put(Keys.RESULT_DATE, obj.get("date"));
			finalMap.put(Keys.API, APIType.HOST);
			finalMap.put(Keys.CALL_EXECUTED, true);

            // Store the new fetched data.
			db.saveData(finalMap);
		}
		else {
			// Add old data to final map
			finalMap.put(Keys.SUCCESS, true);
			finalMap.put(Keys.RATES, ratesMap);
			finalMap.put(Keys.RESULT_DATE, dateMap);
			finalMap.put(Keys.API, apiMap);
			finalMap.put(Keys.CALL_EXECUTED, false);
		}

		return new JSONObject(finalMap);
	}

	/**
	 * Get all latest exchange rates for a specified currency.
	 * @param db Database of class ExchangeDB storing all the exchange rates.
	 * @param currency Currency from which to exchange.
	 * @return Result field of JsonObject as string.
	 */
	@Override
	public JSONObject getAllExchangeRates(ExchangeDB db, String currency) {
		// Check for format of Strings
		if (!Check.isCurrencyCode(currency)) {
			return new JSONObject(Check.formatWrongCurrencyFormatAnswer());
		}

		String urlStr = String.format("%s/latest?base=%s", baseUrl, currency);
		JSONObject obj = Connections.requestAPICall(urlStr);

		Map<Keys, Object> finalMap = new HashMap<>();

		finalMap.put(Keys.SUCCESS, obj.get("success"));
		finalMap.put(Keys.CURRENCY_FROM, obj.get("base"));
		finalMap.put(Keys.RATES, obj.get("rates"));
		finalMap.put(Keys.RESULT_DATE, obj.get("date"));
		finalMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
        finalMap.put(Keys.CALL_EXECUTED, true);
		finalMap.put(Keys.API, APIType.HOST);

		// Store the new fetched data.
		db.saveData(finalMap);

		return new JSONObject(finalMap);		
	}

	/**
	 * Convert currency from one to another.
	 * @param db Database of class ExchangeDB storing all the exchange rates.
	 * @param from Currency from which to convert.
	 * @param to Currency to convert to.
	 * @param amount Amount of the first currency to convert to the second.
	 * @return Result field of JsonObject as string.
	 */
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
			String urlStr = String.format("%s/convert?from=%s&to=%s&amount=%d", baseUrl, from, to, amount);
			JSONObject obj = Connections.requestAPICall(urlStr);
			
 			finalMap.put(Keys.SUCCESS, obj.get("success"));
        	finalMap.put(Keys.RESULT, obj.get("result"));
			finalMap.put(Keys.RATES, ((JSONObject) obj.get("info")).get("rate"));
        	finalMap.put(Keys.RESULT_DATE, obj.get("date"));
			finalMap.put(Keys.CALL_EXECUTED, true);
			finalMap.put(Keys.API, APIType.HOST);

			// Store the new fetched data.
			db.saveConversionData(from, to, amount, time, to, APIType.HOST);
		}

		return new JSONObject(finalMap);
	}
}
