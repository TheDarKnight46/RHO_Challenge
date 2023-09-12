package com.rho.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import com.rho.model.enums.APIType;
import com.rho.model.enums.Keys;

public class ExchangeDB {
    private List<Exchange> exchanges = new ArrayList<>();

    public List<Exchange> checkExchangeUpdateState(String from, String target) {
        updateExchangeStates(from, target);

        List<Exchange> storedExchanges = new ArrayList<>();

        for (Exchange e : exchanges) {
            for (String to : target.replaceAll(" ", "target").split(",")) {
                if (e.getFrom().equals(from) && e.getTo().equals(to)) {
                    if (!e.getOutdated()) {
                        storedExchanges.add(e);
                    }
                }
            }
        }

        return storedExchanges;
    }

    private void updateExchangeStates(String from, String target) {
        for (String t : target.replaceAll(" ", "target").split(",")) {
            Exchange exchange = findExchangeRate(from, t);
            
            LocalTime now = LocalTime.now();
            int hour, min;

            hour = now.getHour();
            min = now.getMinute();

            if ((int) exchange.getRequestTime().get("hour") > hour || (int) exchange.getRequestTime().get("hour") < hour) {
                exchange.setOutdated(true);
            }
            else {
                int dif = min - (int) exchange.getRequestTime().get("min");
                if (dif >= 1) {
                    exchange.setOutdated(true);
                }
                else {
                    exchange.setOutdated(false);
                }
            }
        }
    }

    public Exchange findExchangeRate(String from, String to) {
        for (Exchange e : exchanges) {
            if (e.getFrom().equals(from) && e.getTo().equals(to)) {
                    return e;
                }
            }

        return null;
    }

    public void saveConversionData(String from, String to, double rate, Map<String, Integer> time, String date, APIType source) {
        Exchange e = findExchangeRate(from, to);

        if (e != null) {
            e.editRate(rate, date, time, source);
        }
        else {
            exchanges.add(new Exchange(from, to, rate, date, time, source));
        }       
    }

    @SuppressWarnings("unchecked")
    public void saveData(Map<Keys, Object> valueMap) { // TODO - TEST
        Map<String, Double> ratesMap = (Map<String, Double>) valueMap.get(Keys.RATES);
        String date = (String) valueMap.get(Keys.RESULT_DATE);
        APIType source = (APIType) valueMap.get(Keys.API); // error here
        String from = (String) valueMap.get(Keys.CURRENCY_FROM);
        Map<String, Integer> time = (Map<String, Integer>) valueMap.get(Keys.REQUEST_TIME);

        Iterator<String> keyIt = ratesMap.keySet().iterator();
        Iterator<Double> valueIt = ratesMap.values().iterator();
       
        while (keyIt.hasNext()) {
            String to = keyIt.next();
            
            if (!from.equalsIgnoreCase(to)) {
                Double rate = valueIt.next();
                Exchange e = findExchangeRate(from, to);

                if (e != null) {
                    e.editRate(rate, date, time, source);
                }
                else {
                    exchanges.add(new Exchange(from, to, rate, date, time, source));
                }
            }
            else { // This was required due to API Ayr sending Long as the first rate in JSON instead of Double
                valueIt.next();
            }
        }
    }

    public void addExchangeRate(String from, String to, double rate, String date, Map<String, Integer> time, APIType source) {
        Exchange e = findExchangeRate(from, to);
        if (e != null) {
            e.editRate(rate, date, time, source);
        }
        else {
            exchanges.add(new Exchange(from, to, rate, date, time, source));
        }
    }

    public void addBulkExchangeRate(JSONObject obj, APIType source) {
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
                addExchangeRate(from, to, rate, date, time, source);
            }
        }
    }

    public JSONObject getExchangeRate(String from, String to) {
        Exchange e = findExchangeRate(from, to);
        return e.getExchange();
    }   
    
    public List<Exchange> getExchanges() {
        return exchanges;
    }
}
