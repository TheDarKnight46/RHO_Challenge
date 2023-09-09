package com.rho.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.rho.api.ExchangeRateAPI;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPIController {
    ExchangeRateAPI api;
    
    public RestAPIController() {
        api = new ExchangeRateAPI();
    }

    /**
     * @param currency
     * @param targets
     * @param amount
     * @return
     */
    @GetMapping("/rates")
    public JSONObject getExchangeRates(String currency, String targets, int amount) {
        JSONObject obj = api.getExchangeRates(currency, targets, amount);
        Map<String, Object> valueMap = new HashMap<>();

        valueMap.put("Currency", obj.get("base"));
        valueMap.put("Rates", obj.get("rates"));
        valueMap.put("Date", obj.get("date"));

        return new JSONObject(valueMap);
    }

    /**
     * @param currency
     * @param amount
     * @return
     */
    @GetMapping("/allrates")
    public JSONObject getAllExchangeRates(String currency, int amount) {
        JSONObject obj = api.getAllExchangeRates(currency, amount);
        Map<String, Object> valueMap = new HashMap<>();

        valueMap.put("Currency", obj.get("base"));
        valueMap.put("Rates", obj.get("rates"));
        valueMap.put("Date", obj.get("date"));

        return new JSONObject(valueMap);
    }

    /**
     * @param from
     * @param to
     * @param amount
     * @return
     */
    @GetMapping("/convert")
    public JSONObject convertCurrency(String from, String to, int amount) {
        JSONObject obj = api.convertCurrency(from, to, amount);
        JSONObject query = (JSONObject) obj.get("query");

        Map<String, Object> valueMap = new HashMap<>();

        valueMap.put("Currency From", query.get("from"));
        valueMap.put("Currency To", query.get("to"));
        valueMap.put("Original Amount", query.get("amount"));

        valueMap.put("Result", obj.get("result"));
        valueMap.put("Date", obj.get("date"));

        return new JSONObject(valueMap);
    }

    /**
     * @param from
     * @param targets
     * @param amount
     * @return
     */
    @GetMapping("/convert/multi")
    public JSONObject convertCurrencyToSeveral(String from, String targets, int amount) {
        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.split(",")));
        JSONObject obj = new JSONObject();

        for (String currency : symbols) {
            api.convertCurrency(from, currency, amount);
            
        }


        
        
        return obj;
    }

    
}
