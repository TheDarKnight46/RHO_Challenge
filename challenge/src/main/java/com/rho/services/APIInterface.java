package com.rho.services;

import org.json.simple.JSONObject;

import com.rho.model.ExchangeDB;

public interface APIInterface {
    
    public JSONObject getExchangeRates(ExchangeDB db, String currency, String targets);
    public JSONObject getAllExchangeRates(ExchangeDB db, String currency);
    public JSONObject convertCurrency(ExchangeDB db, String from, String to, double amount);
    
}
