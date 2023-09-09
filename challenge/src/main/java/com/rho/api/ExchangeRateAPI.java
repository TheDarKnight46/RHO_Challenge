package com.rho.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

public class ExchangeRateAPI {
    
    public ExchangeRateAPI() {}

	/**
	 * Parses an HttpURLConncetion request to a JSONObject.
	 * @param request HttpURLConnection. Request must be connected before sending as parameter.
	 * @return JsonObject extracted from the request.
	 */
    private JsonObject parseJsonObject(HttpURLConnection request) {
		try {
			JsonParser jp = Json.createParser(new InputStreamReader((InputStream) request.getContent()));
			return jp.getObject();
		}
		catch (IOException e) {
			System.out.println("IOEsception - request.getContent()");
			return null;
		}
	}

	/**
	 * Request API call from the URL provided.
	 * @param urlStr String of the URL where the request is gonna be made.
	 * @return JsonObject as string.
	 */
	private String requestAPICall(String urlStr) {
		try {
			URI uri = new URI(urlStr);
			URL url = uri.toURL();
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();

			return parseJsonObject(request).get("result").toString();
		}
		catch (IOException | URISyntaxException e) {
			return null;
		}
	}

	/**
	 * Get latests exchange rates. Request URL: https://api.exchangerate.host/latest.
	 * @return JsonObject as string. 
	 */
	public String getLatestRates() {
		return requestAPICall("https://api.exchangerate.host/latest");	
	}

	/**
	 * Convert currency from one to another.
	 * @param from Currency from which to convert.
	 * @param to Currency to convert to.
	 * @return JsonObject as string.
	 */
	public String convertCurrency(String from, String to) {
		String urlStr = String.format("https://api.exchangerate.host/convert?from=%s&to=%s", from, to);
		return requestAPICall(urlStr);
	}

	/**
	 * Get historical rates since a specific date.
	 * @param month Month from when to get the historical rates. 
	 * @param year Year from when to get the historical rates.
	 * @return JsonObject as string.
	 */
	public String historicalRates(String month, String year) {
		String urlStr = String.format("https://api.exchangerate.host/%s-%s-01", year, month);
		return requestAPICall(urlStr);
	}
}
