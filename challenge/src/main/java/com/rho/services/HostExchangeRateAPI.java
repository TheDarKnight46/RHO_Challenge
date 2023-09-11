package com.rho.services;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.rho.model.Keys;

//https://exchangerate.host/#/#docs

@Service
public class HostExchangeRateAPI implements APIInterface {

	private final String baseUrl = "https://api.exchangerate.host/";
    
    public HostExchangeRateAPI() {}

	/**
	 * Get latest exchange rates for a specifed currency and target
	 * @param currency Currency from which to exchange.
	 * @param target List of target currencies to get the exchange rate.
	 * @param amount Amount of currency to exchange.
	 * @return Result field of JsonObject as string.
	 */
	@Override
	public JSONObject getExchangeRates(String currency, String targets) {
		String urlStr = String.format("%s/latest?base=%s&symbols=%s", baseUrl, currency, targets);
		JSONObject obj = Connections.requestAPICall(urlStr);
		
		Map<Keys, Object> valueMap = new HashMap<>();

		valueMap.put(Keys.SUCCESS, obj.get("success"));
        valueMap.put(Keys.CURRENCY_FROM, obj.get("base"));
        valueMap.put(Keys.RATES, obj.get("rates"));
        valueMap.put(Keys.RESULT_DATE, obj.get("date"));
		valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
		valueMap.put(Keys.API, "Host");

		return new JSONObject(valueMap);
	}

	/**
	 * Get all latest exchange rates for a specified currency.
	 * @param currency Currency from which to exchange.
	 * @param amount Amount of the currency to exchange.
	 * @return Result field of JsonObject as string.
	 */
	@Override
	public JSONObject getAllExchangeRates(String currency) {
		String urlStr = String.format("%s/latest?base=%s", baseUrl, currency);
		JSONObject obj = Connections.requestAPICall(urlStr);

		Map<Keys, Object> valueMap = new HashMap<>();

		valueMap.put(Keys.SUCCESS, obj.get("success"));
        valueMap.put(Keys.CURRENCY_FROM, obj.get("base"));
        valueMap.put(Keys.RATES, obj.get("rates"));
        valueMap.put(Keys.RESULT_DATE, obj.get("date"));
		valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
		valueMap.put(Keys.API, "Host");

		return new JSONObject(valueMap);
	}

	/**
	 * Convert currency from one to another.
	 * @param from Currency from which to convert.
	 * @param to Currency to convert to.
	 * @return Result field of JsonObject as string.
	 */
	@Override
	public JSONObject convertCurrency(String from, String to, int amount) {
		String urlStr = String.format("%s/convert?from=%s&to=%s&amount=%d", baseUrl, from, to, amount);
		JSONObject obj = Connections.requestAPICall(urlStr);
	
        Map<Keys, Object> valueMap = new HashMap<>();

 		valueMap.put(Keys.SUCCESS, obj.get("success"));
        valueMap.put(Keys.CURRENCY_FROM, from);
        valueMap.put(Keys.CURRENCY_TO, to);
        valueMap.put(Keys.ORIGINAL_AMOUNT, amount);
        valueMap.put(Keys.RESULT, obj.get("result"));
		valueMap.put(Keys.RATES, ((JSONObject) obj.get("info")).get("rate"));
        valueMap.put(Keys.RESULT_DATE, obj.get("date"));
		valueMap.put(Keys.REQUEST_TIME, Connections.getCurrentTime());
		valueMap.put(Keys.API, "Host");

		return new JSONObject(valueMap);
	}
}
