package com.rho.services;

import com.rho.model.ExchangeDB;
import com.rho.model.schemas.CustomSchema;

public interface APIInterface {
    
    public CustomSchema getExchangeRates(ExchangeDB db, String currency, String targets);
    public CustomSchema getAllExchangeRates(ExchangeDB db, String currency);
    public CustomSchema convertCurrency(ExchangeDB db, String from, String to, double amount);
    
}
