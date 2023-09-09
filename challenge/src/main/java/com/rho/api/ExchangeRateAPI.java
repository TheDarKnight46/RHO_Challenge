package com.rho.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExchangeRateAPI implements ExchangeRateAPIInterface {
    
    public ExchangeRateAPI() {}

	/**
	 * Parses an HttpURLConncetion request to a JSONObject.
	 * @param request HttpURLConnection. Request must be connected before sending as parameter.
	 * @return JsonObject extracted from the request.
	 */
    private JSONObject parseJsonObject(HttpURLConnection request) {
		try {
			InputStreamReader isr = new InputStreamReader((InputStream) request.getContent());
			JSONParser jp = new JSONParser();
			return (JSONObject) jp.parse(isr);
		}
		catch (IOException | ParseException e) {
			System.out.println("IOEsception - request.getContent()");
			return null;
		}
	}

	/**
	 * Request API call from the URL provided.
	 * @param urlStr String of the URL where the request is gonna be made.
	 * @return JsonObject as string.
	 */
	private JSONObject requestAPICall(String urlStr) {
		try {
			URI uri = new URI(urlStr);
			URL url = uri.toURL();
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();

			return parseJsonObject(request);
		}
		catch (IOException | URISyntaxException e) {
			return null;
		}
	}

/* 
	a. Get exchange rate from Currency A to Currency B
	b. Get all exchange rates from Currency A
	c. Get value conversion from Currency A to Currency B
	d. Get value conversion from Currency A to a list of supplied currencies
*/

	/**
	 * Get latest exchange rates for a specifed currency and target
	 * @param currency Currency from which to exchange.
	 * @param target List of target currencies to get the exchange rate.
	 * @param amount Amount of currency to exchange.
	 * @return Result field of JsonObject as string.
	 */
	@Override
	public JSONObject getExchangeRates(String currency, String targets, int amount) {
		//String symbols = getCurrenciesCommaSeparated(targets);
		String urlStr = String.format("https://api.exchangerate.host/latest?base=%s&symbols=%s&amount=%d", currency, targets, amount);
		return requestAPICall(urlStr);
	}

	/**
	 * Get all latest exchange rates for a specified currency.
	 * @param currency Currency from which to exchange.
	 * @param amount Amount of the currency to exchange.
	 * @return Result field of JsonObject as string.
	 */
	@Override
	public JSONObject getAllExchangeRates(String currency, int amount) {
		String urlStr = String.format("https://api.exchangerate.host/latest?base=%s&amount=%d", currency, amount);
		return requestAPICall(urlStr);	
	}

	/**
	 * Convert currency from one to another.
	 * @param from Currency from which to convert.
	 * @param to Currency to convert to.
	 * @return Result field of JsonObject as string.
	 */
	@Override
	public JSONObject convertCurrency(String from, String to, int amount) {
		String urlStr = String.format("https://api.exchangerate.host/convert?from=%s&to=%s&amount=%d", from, to, amount);
		return requestAPICall(urlStr);
	}

	/**
	 * Convert currency from one to many.
	 * @param from 
	 * @param to
	 * @param currencies
	 * @return Result field of JsonObject as string.
	 */
	/*@Override
	public JSONObject convertCurrencyToSeveral(String from, String to, String targets, int amount) {
		// for (String currency : targets) {
			
		// }
		//String symbols = getCurrenciesCommaSeparated(targets);
		String urlStr = String.format("https://api.exchangerate.host/convert?from=%s&to=%s&symbols=%s&amount=%d", from, to, targets, amount);
		return requestAPICall(urlStr);
	}*/
}
