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

//https://www.exchangerate-api.com/docs/standard-requests

@Service
public class AyrExchangeRateAPI implements APIInterface {

    private final String apiKey = "0f5de615a9b5ae5e11c27252";
    private final String baseUrl = "https://v6.exchangerate-api.com/v6";

    public AyrExchangeRateAPI() {}

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
        boolean save = false;
        RequestAnswer answer;

		// Reduce Calls Mechanism
		List<Exchange> oldExchanges = db.checkExchangeUpdateState(currency, targets);
		for (Exchange e : oldExchanges) { // If list size is 0, loop is not executed.
			String to = e.getTo();

			apiMap.put(to, e.getSource());
			ratesMap.put(to, e.getRate());
		}

        String[] symbols = targets.replaceAll(" ", "").split(",");
        if (oldExchanges.size() < symbols.length) {
			// Request new data from API
            String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);
            JSONObject obj = Connections.requestAPICall(urlStr);

            Map<String, Double> currencyMap = new HashMap<>();
            JSONObject rates = (JSONObject) obj.get("conversion_rates");

            for (String symbol : symbols) {
                currencyMap.put(symbol, (double) rates.get(symbol));
            }

            boolean success = ((String) obj.get("result")).equals("success");
            answer = new RequestAnswer(success, true);
            answer.setRates(currencyMap);
            for (String s : symbols) {
				answer.addApi(APIType.AYR, s);
			}

            save = true;
        }
        else {
            // Add old data to final map
            answer = new RequestAnswer(true, false);
            answer.setRates(ratesMap);
            answer.setApi(apiMap);
        }

        // Add standard data to final map
        answer.setCurrencyFrom(currency);     
        answer.setCurrencyTo(symbols);
        answer.setRequestTime(Connections.getCurrentTime());
        
        if (save) {
            // Store the new fetched data.
            db.saveData(answer);
        }

        return answer;
    }

	@SuppressWarnings("unchecked")
    @Override
    public CustomSchema getAllExchangeRates(ExchangeDB db, String currency) {
        // Check for format of Strings
		if (!Check.isCurrencyCode(currency)) {
			return Check.formatWrongCurrencyFormatAnswer();
		}

        // Request new data from API
        String urlStr = String.format("%s/%s/latest/%s", baseUrl, apiKey, currency);		
		JSONObject obj = Connections.requestAPICall(urlStr);

        boolean success = ((String) obj.get("result")).equals("success");
        RequestAnswer answer = new RequestAnswer(success, true); 
        
        answer.setCurrencyFrom(currency);
        answer.setRates((Map<String, Double>) obj.get("conversion_rates"));
		answer.addApi(APIType.AYR, "All");

        // Store the new fetched data.
        db.saveData(answer);

		return answer;
    }

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
			// Add old data to final map
			double rate = oldExchange.getRate();

            answer = new RequestAnswer(true, false);
            answer.addResult(rate*amount, to);
            answer.addRate(rate, to);
            answer.addApi(oldExchange.getSource(), to);
            answer.setRequestTime(Connections.getCurrentTime());
        }
        else {
			// Request new data from API
            String urlStr = String.format("%s/%s/pair/%s/%s/%f", baseUrl, apiKey, from, to, amount);		
            JSONObject obj = Connections.requestAPICall(urlStr);

            boolean success = ((String) obj.get("result")).equals("success");
            answer = new RequestAnswer(success, true);
            answer.addRate((double) obj.get("conversion_rate"), to);
            answer.addResult((double) obj.get("conversion_result"), to);
            answer.addApi(APIType.AYR, to);
		    answer.setRequestTime(Connections.getCurrentTime());

            // Store the new fetched data.
            db.saveConversionData(from, to, amount, answer.getRequestTime(), APIType.AYR);
        }

        // Add standard data to answer
        answer.setAmount(amount);
		answer.setCurrencyFrom(from);
		answer.addCurrencyTo(to);

        return answer;
    }
}