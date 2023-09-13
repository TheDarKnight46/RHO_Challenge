package com.rho.model.schemas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rho.model.enums.APIType;

public class RequestAnswer extends CustomSchema {
    private String currencyFrom = "";
    private List<String> currencyTo = new ArrayList<>();
    private double amount = 0;
    private Map<String, Double> results = new HashMap<>();
    private Map<String, Double> rates = new HashMap<>();
    private Map<String, Integer> requestTime = new HashMap<>();
    private Map<String, APIType> api = new HashMap<>();

    public RequestAnswer(boolean success,
                        boolean callExecuted) {
        super(success, callExecuted);
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public List<String> getCurrencyTo() {
        return currencyTo;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public double getAmount() {
        return amount;
    }
    
    public Map<String, APIType> getApi() {
        return api;
    }

    public Map<String, Integer> getRequestTime() {
        return requestTime;
    }

    public Map<String, Double> getResults() {
        return results;
    }

    public boolean isCallExecuted() {
        return super.isCallExecuted();
    }

    public boolean isSuccess() {
        return super.isSuccess();
    }

    // =========

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void addApi(APIType api, String to) {
        this.api.put(to, api);
    }

    public void setApi(Map<String, APIType> api) {
        this.api = api;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public void setCurrencyTo(String[] currencies) {
        for (String t : currencies) {
            this.currencyTo.add(t);
        }
    }

    public void addCurrencyTo(String currency) {
        this.currencyTo.add(currency);
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public void addRate(Double rates, String to) {
        this.rates.put(to, rates);
    }

    public void joinRates(Map<String, Double> newRates) {
        Iterator<String> keyIt = newRates.keySet().iterator();
        Iterator<Double> valueIt = newRates.values().iterator();

        while (keyIt.hasNext()) {
            try {
                String key = keyIt.next();
                Double val = valueIt.next();
                
                rates.put(key, val);
            } catch (ClassCastException e) {}
        }
    }

    public void setResults(Map<String, Double> results) {
        this.results = results;
    }

    public void addResult(Double result, String to) {
        this.results.put(to, result);
    }

    public void joinResults(Map<String, Double> newResults) {
        Iterator<String> keyIt = newResults.keySet().iterator();
        Iterator<Double> valueIt = newResults.values().iterator();

        while (keyIt.hasNext()) {
            try {
                String key = keyIt.next();
                Double val = valueIt.next();
                
                rates.put(key, val);
            } catch (ClassCastException e) {}
        }
    }

    public void setRequestTime(Map<String, Integer> requestTime) {
        this.requestTime = requestTime;
    }
}
