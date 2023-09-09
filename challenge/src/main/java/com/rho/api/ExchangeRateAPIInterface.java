package com.rho.api;

import org.json.simple.JSONObject;

public interface ExchangeRateAPIInterface {
    
    public JSONObject getExchangeRates(String currency, String targets, int amount);
    public JSONObject getAllExchangeRates(String currency, int amount);
    public JSONObject convertCurrency(String from, String to, int amount);
    
}
