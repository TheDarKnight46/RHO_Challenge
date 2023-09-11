package com.rho.api;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

//https://exchangerate.host/#/#docs

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
		
		Map<String, Object> valueMap = new HashMap<>();

		valueMap.put("Success", obj.get("success"));
        valueMap.put("Currency", obj.get("base"));
        valueMap.put("Rates", obj.get("rates"));
        valueMap.put("Date", obj.get("date"));
		
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
		String urlStr = String.format("%s/latest?base=%s&amount=%d", baseUrl, currency);
		JSONObject obj = Connections.requestAPICall(urlStr);

		Map<String, Object> valueMap = new HashMap<>();

		valueMap.put("Success", obj.get("success"));
        valueMap.put("Currency", obj.get("base"));
        valueMap.put("Rates", obj.get("rates"));
        valueMap.put("Date", obj.get("date"));

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
		String urlStr = String.format("%s/convert?from=%s&to=%s&amount=%d", from, to, amount);
		JSONObject obj = Connections.requestAPICall(urlStr);
	
        Map<String, Object> valueMap = new HashMap<>();

 		valueMap.put("Success", obj.get("success"));
        valueMap.put("Currency From", from);
        valueMap.put("Currency To", to);
        valueMap.put("Original Amount", amount);
        valueMap.put("Result", obj.get("result"));
		valueMap.put("Rate", ((JSONObject) obj.get("info")).get("rate"));
        valueMap.put("Date", obj.get("date"));

		return new JSONObject(valueMap);
	}
}
