package com.rho.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class Connections {
	
    /**
	 * Parses an HttpURLConncetion request to a JSONObject.
	 * @param request HttpURLConnection. Request must be connected before sending as parameter.
	 * @return JsonObject extracted from the request.
	 */
    private static JSONObject parseJsonObject(HttpURLConnection request) {
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
	public static JSONObject requestAPICall(String urlStr) {
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

	public static Map<String, Integer> getCurrentTime() {
		LocalTime now = LocalTime.now();
		Map<String, Integer> time = new HashMap<>();
		
		time.put("Hour", now.getHour());
		time.put("Minute", now.getMinute());
		time.put("Seconds", now.getSecond());

		return time;
	}
}
