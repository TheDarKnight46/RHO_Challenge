package com.rho.api;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

//currency api

public class CurrencyExchangeAPI implements APIInterface {
    private final String baseUrl = "https://api.currencyapi.com/v3";
    private final String apiKey = "cur_live_l1BY0Qops7hkmqeSIyzQpOh09WN4xPXJFaynrpA7";

    //https://api.currencyapi.com/v3/latest?apikey=cur_live_l1BY0Qops7hkmqeSIyzQpOh09WN4xPXJFaynrpA7&base_currency=EUR

    @Override
    public JSONObject getExchangeRates(String currency, String targets) {
        String urlStr = String.format("%s/latest?apikey=%s&base_currency=%s&currencies=%s", baseUrl, apiKey, currency, targets);		
        JSONObject obj = Connections.requestAPICall(urlStr);

        Map<String, Object> valueMap = new HashMap<>();

        //valueMap.put("Date", obj.get("last_updated_at"));

        //maps needs changing
        //valueMap.put("Success", obj.get("success"));
        //valueMap.put("Currency", obj.get("base"));
        //valueMap.put("Rates", obj.get("rates"));
        //valueMap.put("Date", obj.get("date"));

        return new JSONObject(valueMap);
    }

    @Override
    public JSONObject getAllExchangeRates(String currency) {
        String urlStr = String.format("%s/latest?apikey=%s&base_currency=%s", baseUrl, apiKey, currency);		
        JSONObject obj = Connections.requestAPICall(urlStr);

        Map<String, Object> valueMap = new HashMap<>();

        //maps needs changing
        //valueMap.put("Success", obj.get("success"));
        //valueMap.put("Currency", obj.get("base"));
        //valueMap.put("Rates", obj.get("rates"));
        //valueMap.put("Date", obj.get("date"));

        return new JSONObject(valueMap);
    }

    @Override
    public JSONObject convertCurrency(String from, String to, int amount) {
        //this needs to be hand calculated


        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertCurrency'");
    }
    
}
