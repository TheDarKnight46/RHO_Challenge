package com.rho.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.rho.model.Exchange;
import com.rho.model.ExchangeDB;
import com.rho.model.enums.APIType;
import com.rho.model.schemas.CustomSchema;
import com.rho.model.schemas.RequestAnswer;

//https://exchangerate.host/#/#docs

@Service
public class HostExchangeRateAPI implements APIInterface {

	private final String baseUrl = "https://api.exchangerate.host/";
	
    public HostExchangeRateAPI() {}

	/**
	 * Get latest exchange rates for a specified currency and target. API: Host
	 * @param db Database of class ExchangeDB storing all the exchange rates.
	 * @param currency Currency from which to exchange.
	 * @param target List of target currencies to get the exchange rate.
	 * @return Result as a CustomSchema.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CustomSchema getExchangeRates(ExchangeDB db, String currency, String targets) {
		// Check for format of Strings
		if (!Check.isCurrencyCode(currency)) {
			return Check.formatWrongCurrencyFormatAnswer();
		}
		for (String t : targets.replaceAll(" ", "").split(targets)) {
			if (!Check.isCurrencyCode(t)) {
				return Check.formatWrongCurrencyFormatAnswer();
			}
		}

		Map<String, APIType> apiMap = new HashMap<>();
		Map<String, Double> ratesMap = new HashMap<>();

		// Reduce Calls Mechanism
		List<Exchange> oldExchanges = db.checkExchangeUpdateState(currency, targets);
		for (Exchange e : oldExchanges) { // If list size is 0, loop is not executed.
			String to = e.getTo();

			apiMap.put(to, e.getSource());
			ratesMap.put(to, e.getRate());
		}
		
		RequestAnswer answer;
		boolean save = false;
		String[] symbols = targets.replaceAll(" ", "").split(",");
		// Check if API call is still required
		if (oldExchanges.size() < symbols.length) {
			// Request new data from API
			String urlStr = String.format("%s/latest?base=%s&symbols=%s", baseUrl, currency, targets);
			JSONObject obj = Connections.requestAPICall(urlStr);

			answer = new RequestAnswer((boolean) obj.get("success"), true);
			answer.setRates((Map<String, Double>) obj.get("rates"));
			for (String s : symbols) {
				answer.addApi(APIType.HOST, s);
			}

			save = true;
		}
		else {
			// Add old data to answer
			answer = new RequestAnswer(true, false);
			answer.setRates(ratesMap);
			answer.setApi(apiMap);
		}

		// Add standard data to answer
		answer.setCurrencyFrom(currency);
		answer.setCurrencyTo(symbols);
		answer.setRequestTime(Connections.getCurrentTime());

		if (save) {
			// Store the new fetched data.
			db.saveData(answer);
		}

		return answer;
	}

	/**
	 * Get all latest exchange rates for a specified currency. API: Host
	 * @param db Database of class ExchangeDB storing all the exchange rates.
	 * @param currency Currency from which to exchange.
	 * @return Result as a CustomSchema.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CustomSchema getAllExchangeRates(ExchangeDB db, String currency) {
		// Check for format of Strings
		if (!Check.isCurrencyCode(currency)) {
			return Check.formatWrongCurrencyFormatAnswer();
		}

		// Request new data from API
		String urlStr = String.format("%s/latest?base=%s", baseUrl, currency);
		JSONObject obj = Connections.requestAPICall(urlStr);
		RequestAnswer answer = new RequestAnswer((boolean) obj.get("success"), true);

		answer.setCurrencyFrom(currency);
		answer.setRates((Map<String, Double>) obj.get("rates"));
		answer.setRequestTime(Connections.getCurrentTime());
		answer.addApi(APIType.HOST, "All");

		// Store the new fetched data.
		db.saveData(answer);

		return answer;		
	}

	/**
	 * Convert currency from one to another. API: Host
	 * @param db Database of class ExchangeDB storing all the exchange rates.
	 * @param from Currency from which to convert.
	 * @param to Currency to convert to.
	 * @param amount Amount of the first currency to convert to the second.
	 * @return Result as a CustomSchema.
	 */
	@Override
	public CustomSchema convertCurrency(ExchangeDB db, String from, String to, double amount) {
		// Check for format of Strings
		if (!Check.isCurrencyCode(from)) {
			return Check.formatWrongCurrencyFormatAnswer();
		}
		if (!Check.isCurrencyCode(to)) {
			return Check.formatWrongCurrencyFormatAnswer();
		}

		RequestAnswer answer;

		// Reduce Calls Mechanism
		Exchange oldExchange = db.findExchangeRate(from, to);

        // Check if there is an old exchange updated
		if (oldExchange != null && !oldExchange.getOutdated()) {
			// Add old data to answer
			double rate = oldExchange.getRate();

			answer = new RequestAnswer(true, false);
			answer.addResult(rate*amount, to);
			answer.addRate(rate, to);
			answer.addApi(oldExchange.getSource(), to);
			answer.setRequestTime(Connections.getCurrentTime());
		} 
		else {
			// Request new data from API
			String urlStr = String.format("%s/convert?from=%s&to=%s&amount=%f", baseUrl, from, to, amount);
			JSONObject obj = Connections.requestAPICall(urlStr);
			
			answer = new RequestAnswer(true, true);
			answer.addResult((double) obj.get("result"), to);
			answer.addRate((double) ((JSONObject) obj.get("info")).get("rate"), to);
			answer.addApi(APIType.HOST, to);
			answer.setRequestTime(Connections.getCurrentTime());

			// Store the new fetched data. 
			db.saveConversionData(from, to, amount, answer.getRequestTime(), APIType.HOST);
		}

		// Add standard data to answer
		answer.setAmount(amount);
		answer.setCurrencyFrom(from);
		answer.addCurrencyTo(to);

		return answer;
	}
}
