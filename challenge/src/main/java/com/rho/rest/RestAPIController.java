package com.rho.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.rho.api.AYRTechExchangeRateAPI;
import com.rho.api.HostExchangeRateAPI;
import com.rho.api.CurrencyExchangeAPI;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPIController { // the app compiles but i cant launch it. resume there
    HostExchangeRateAPI hostAPI;
    AYRTechExchangeRateAPI ayrAPI;
    CurrencyExchangeAPI layerAPI;
    
    public RestAPIController() {
        hostAPI = new HostExchangeRateAPI();
        ayrAPI = new AYRTechExchangeRateAPI();
        layerAPI = new CurrencyExchangeAPI();
    }

    @GetMapping("/")
    public String index() {
        return "Hello world";
    }

    /**
     * @param currency
     * @param targets
     * @param amount
     * @return
     */
    @GetMapping("/rates")
    public JSONObject getExchangeRates(String currency, String targets, int amount, String apiType) {
        JSONObject obj;
        Map<String, JSONObject> valueMap = new HashMap<>();

        switch (apiType) { //switch with String is now possible
            case "host":        
                obj = hostAPI.getExchangeRates(currency, targets);
                break;  
            case "ayr":
                obj = ayrAPI.getExchangeRates(currency, targets);
                break;
            case "layer":
                obj = layerAPI.getExchangeRates(currency, targets);
                break;
            default:
                return null;
        }

        valueMap.put(apiType, obj);

        return new JSONObject(valueMap);
    }

    /**
     * @param currency
     * @param amount
     * @return
     */
    @GetMapping("/allrates")
    public JSONObject getAllExchangeRates(String currency, int amount, String apiType) {
        JSONObject obj;
        Map<String, JSONObject> valueMap = new HashMap<>();

        switch (apiType) { //switch with String is now possible
            case "host":        
                obj = hostAPI.getAllExchangeRates(currency);
                break;  
            case "ayr":
                obj = ayrAPI.getAllExchangeRates(currency);
                break;
            case "layer":
                obj = layerAPI.getAllExchangeRates(currency);
                break;
            default:
                return null;
        }

        valueMap.put(apiType, obj);

        return new JSONObject(valueMap);
    }

    /**
     * @param from
     * @param to
     * @param amount
     * @return
     */
    @GetMapping("/convert")
    public JSONObject convertCurrency(String from, String to, int amount, String apiType) {
        JSONObject obj;
        Map<String, JSONObject> valueMap = new HashMap<>();

        switch (apiType) { //switch with String is now possible
            case "host":        
                obj = hostAPI.convertCurrency(from, to, amount);
                break;  
            case "ayr":
                obj = ayrAPI.convertCurrency(from, to, amount);
                break;
            case "layer":
                obj = layerAPI.convertCurrency(from, to, amount);
                break;
            default:
                return null;
        }

        valueMap.put(apiType, obj);

        return new JSONObject(valueMap);
    }

    /**
     * @param from
     * @param targets
     * @param amount
     * @return
     */
    @GetMapping("/convert/multi")
    public JSONObject convertCurrencyToSeveral(String from, String targets, int amount, String apiType) {
        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.split(",")));
        JSONObject obj = new JSONObject();
        Map<String, JSONObject> valueMap = new HashMap<>();

        for (String currency : symbols) {
            switch (apiType) {
                case "host":
                    obj = hostAPI.convertCurrency(from, currency, amount);
                    break;
                case "ayr":
                    obj = ayrAPI.convertCurrency(from, currency, amount);
                    break;
                case "layer":
                    obj = layerAPI.convertCurrency(from, currency, amount);
                    break;
                default:
                    break;
            }

            valueMap.put(apiType, obj);            
        }

        return new JSONObject(valueMap);
    }

    
}
