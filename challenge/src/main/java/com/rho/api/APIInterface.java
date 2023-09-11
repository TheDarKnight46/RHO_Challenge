package com.rho.api;

import org.json.simple.JSONObject;

public interface APIInterface {
    
    public JSONObject getExchangeRates(String currency, String targets);
    public JSONObject getAllExchangeRates(String currency);
    public JSONObject convertCurrency(String from, String to, int amount);
    
}
