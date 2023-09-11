package com.rho.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public abstract class ExchangeDatabase {
    public static List<Exchange> exchanges = new ArrayList<>();

    public static Exchange findExchangeRate(String from, String to) {
        for (Exchange conv : exchanges) {
            if (conv.getFrom().equals(from) && conv.getTo().equals(to)) {
                return conv;
            }
        }
        return null;
    } 

    public static void addExchangeRate(String from, String to, float rate, String date, Map<String, Integer> time, APIType source) {
        Exchange conv = findExchangeRate(from, to);
        if (conv != null) {
            conv.editRate(rate, date, time, source);
        }
        else {
            exchanges.add(conv);
        }
    }

    public static void addBulkExchangeRate(JSONObject obj, APIType source) {
        String from = (String) obj.get("Currency");
        JSONObject rates = (JSONObject) obj.get("Rates");

        String date = (String) obj.get("Result Date");
        @SuppressWarnings("unchecked") //remove later
        Map<String, Integer> time = (Map<String, Integer>) obj.get("Request Time");

        @SuppressWarnings("unchecked") //remove later
        Set<String> rateNames = rates.keySet();
        @SuppressWarnings("unchecked") //remove later
        Set<Integer> rateValues = rates.entrySet();
        Iterator<String> itNames = rateNames.iterator();

        while (itNames.hasNext()) {
            Iterator<Integer> itRates = rateValues.iterator();
            String to = itNames.next();

            while (itRates.hasNext()) {
                int rate = itRates.next();
                
                Exchange conv = findExchangeRate(from, to);
                if (conv != null) {
                    conv.editRate(rate, date, time, source);
                }
                else {
                    exchanges.add(conv);
                }
            }
        }
    }

    public static JSONObject getExchangeRate(String from, String to) {
        Exchange conv = findExchangeRate(from, to);
        return conv.getRate();
    }    
}
