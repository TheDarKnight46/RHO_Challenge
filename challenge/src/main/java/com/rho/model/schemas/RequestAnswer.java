package com.rho.model.schemas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestAnswer {
    private boolean success = false;
    private boolean callExecuted = false;
    private String currencyFrom = "";
    private List<String> currencyTo = new ArrayList<>();
    private double amount = 0;
    private List<Double> result = new ArrayList<>();
    private List<Double> rates = new ArrayList<>();;
    private Map<String, Integer> requestTime = new HashMap<>();
    private String date = "";
    private List<Double> api = new ArrayList<>();

    public RequestAnswer(boolean success,
                        String currencyFrom,
                        List<String> currencyTo,
                        double amount,
                        List<Double> results, 
                        List<Double> rates,
                        Map<String, Integer> requestTime,
                        String date,
                        List<Double> api,
                        boolean callExecuted) {
        this.success = success;
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.amount = amount;
        this.result = results;
        this.rates = rates;
        this.requestTime = requestTime;
        this.date = date;
        this.api = api;
        this.callExecuted = callExecuted;
    }

    public double getAmount() {
        return amount;
    }
    
    public List<Double> getApi() {
        return api;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public List<String> getCurrencyTo() {
        return currencyTo;
    }

    public String getDate() {
        return date;
    }

    public List<Double> getRates() {
        return rates;
    }

    public Map<String, Integer> getRequestTime() {
        return requestTime;
    }

    public List<Double> getResult() {
        return result;
    }

    public boolean isCallExecuted() {
        return callExecuted;
    }

    public boolean isSuccess() {
        return success;
    }

}
