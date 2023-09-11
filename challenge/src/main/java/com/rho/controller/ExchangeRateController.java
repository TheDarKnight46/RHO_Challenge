package com.rho.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.rho.model.APIType;
import com.rho.model.Keys;
import com.rho.services.AyrExchangeRateAPI;
import com.rho.services.HostExchangeRateAPI;

@RestController
public class ExchangeRateController {

    private HostExchangeRateAPI hostAPI = new HostExchangeRateAPI();
    private AyrExchangeRateAPI ayrAPI = new AyrExchangeRateAPI();

    @GetMapping("/")
    public String index() {
        return "Welcome";
    }

    // ========= GET ALL RATES =========

    @GetMapping("/rates/all/{currency}") 
    public JSONObject getAllExchangeRates(@PathVariable("currency") String currency) {
        return hostAPI.getAllExchangeRates(currency);
    }

    @GetMapping("/rates/all/{api}/{currency}")
    public JSONObject getAllExchangeRates(@PathVariable("currency") String currency, @PathVariable("api") String api) {
        switch (getEnumApiType(api)) {
            case HOST:
                return hostAPI.getAllExchangeRates(currency);
            case AYR:
                return ayrAPI.getAllExchangeRates(currency);
            default:
                return null;
        }        
    }

    // ========= GET SPECIFIC RATES =========

    @GetMapping("/rates/{currency}&{targets}")
    public JSONObject getExchangeRates(@PathVariable("currency") String currency, @PathVariable("targets") String targets) {
        return hostAPI.getExchangeRates(currency, targets);
    }

    @GetMapping("/rates/{api}/{currency}&{targets}")
    public JSONObject getExchangeRates(@PathVariable("currency") String currency, @PathVariable("targets") String targets, @PathVariable("api") String api) {
        switch (getEnumApiType(api)) {
            case HOST:
                return hostAPI.getExchangeRates(currency, targets);
            case AYR:
                return ayrAPI.getExchangeRates(currency, targets);
            default:
                return null;
        }
    }

    // ========= CONVERT CURRENCY A TO B =========

    @GetMapping("/convert/{from}&{to}&{amount}")
    public JSONObject convertCurrency(@PathVariable("from") String from, @PathVariable("to") String to, @PathVariable("amount") int amount) {
        return hostAPI.convertCurrency(from, to, amount);
    }

    @GetMapping("/convert/{api}/{from}&{to}&{amount}")
    public JSONObject convertCurrency(@PathVariable("from") String from, @PathVariable("to") String to, @PathVariable("amount") int amount, @PathVariable("api") String api) {
        switch (getEnumApiType(api)) {
            case HOST:
                return hostAPI.convertCurrency(from, to, amount);
            case AYR:
                return ayrAPI.convertCurrency(from, to, amount);
            default:
                return null;
        }
    }

    // ========= CONVERT CURRENCY A TO MULTIPLE =========

    @GetMapping("/convert/multi/{from}&{targets}&{amount}")
    public JSONObject convertMultiCurrency(@PathVariable("from") String from, @PathVariable("targets") String targets, @PathVariable("amount") int amount) {
        JSONObject obj = null;
        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.split(",")));
        Map<String, Double> currencyMap = new HashMap<>();
        Map<String, Double> ratesMap = new HashMap<>();

        for (String str : symbols) {
            obj = hostAPI.convertCurrency(from, str, amount);
            currencyMap.put(str, (Double) obj.get(Keys.RESULT));
            ratesMap.put(str, (Double) obj.get(Keys.RATES));
        }
        
        if (obj != null) {
            obj.replace(Keys.RESULT, currencyMap); // TODO - fix
            obj.replace(Keys.RATES, ratesMap); // TODO - fix
            obj.replace(Keys.CURRENCY_TO, targets); // TODO - fix

            return obj;
        }
        return null;
    }

    @GetMapping("/convert/multi/{api}/{from}&{targets}&{amount}")
    public JSONObject convertMultiCurrency(@PathVariable("from") String from, @PathVariable("targets") String targets, @PathVariable("amount") int amount, @PathVariable("api") String api) {
        JSONObject obj = null;
        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.split(",")));
        Map<String, Float> currencyMap = new HashMap<>();
        Map<String, Float> ratesMap = new HashMap<>();

        
        for (String str : symbols) {
            switch (getEnumApiType(api)) {
                case HOST:
                    obj = hostAPI.convertCurrency(from, str, amount);
                    break;
                case AYR:
                    obj = ayrAPI.convertCurrency(from, str, amount);
                    break;
                default:
                    break;
            }
            
            if (obj != null) {
                currencyMap.put(str, (Float) obj.get(Keys.RESULT));
                ratesMap.put(str, (Float) obj.get(Keys.RATES));
            }
        }
          
        if (obj != null) {
            obj.replace(Keys.RESULT, currencyMap); // TODO - fix
            obj.replace(Keys.RATES, ratesMap); // TODO - fix
            obj.replace(Keys.CURRENCY_TO, targets); // TODO - fix

            return obj;
        }
        return null;
    }

    // ========= OTHER METHODS =========

    private APIType getEnumApiType(String type) {
        switch (type) {
            case "host":
                return APIType.HOST;
            case "ayr":
                return APIType.AYR;                
            case "currency":
                return APIType.CURRENCY;
            default:
                return null;
        }
    }
}
