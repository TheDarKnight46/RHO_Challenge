package com.rho.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;

import com.rho.model.enums.APIType;
import com.rho.model.schemas.RequestAnswer;

public class ExchangeDB {
    private List<Exchange> exchanges = new ArrayList<>();

    /**
     * Check update state of every Exchange mentioned in target.
     * @param from Currency to convert from. Exchange ID.
     * @param target Currencies to convert to. Exchange ID.
     * @return List of stored exchanges that match the from and target.
     */
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

    /**
     * Updates the state of the specified Exchanges according to their request time.
     * If an Exchange is more than a minute old, it becomes Outdated.
     * @param from Currency to convert from. Exchange ID.
     * @param target Currencies to convert to. Exchange ID.
     */
    private void updateExchangeStates(String from, String target) {
        for (String t : target.replaceAll(" ", "target").split(",")) {
            Exchange exchange = findExchangeRate(from, t);
            if (exchange == null) {
                continue;
            }
            
            LocalTime now = LocalTime.now();
            int hour, min, sec;

            hour = now.getHour();
            min = now.getMinute();
            sec = now.getSecond();

            if ((int) exchange.getRequestTime().get("Hour") > hour || (int) exchange.getRequestTime().get("Hour") < hour) {
                exchange.setOutdated(true);
            }
            else {
                int difMin = min - (int) exchange.getRequestTime().get("Minute");
                if (difMin > 1) {
                    exchange.setOutdated(true);
                }
                else if (difMin == 1) {
                    if ((int) exchange.getRequestTime().get("Minute") > sec) {
                        exchange.setOutdated(true);
                    }
                    else {
                        exchange.setOutdated(false);
                    }
                }
            }
        }
    }

    /**
     * Find a specific instance of Exchange using its identifiers.
     * @param from Currency to convert from. Exchange ID.
     * @param to Currency to convert to. Exchange ID.
     * @return Return Exchange object if found. If not found, return null.
     */
    public Exchange findExchangeRate(String from, String to) {
        for (Exchange e : exchanges) {
            if (e.getFrom().equals(from) && e.getTo().equals(to)) {
                    return e;
                }
            }

        return null;
    }

    /**
     * Saves newly fetched data used for conversions.
     * @param from Currency from which it was converted.
     * @param to Currency to which it was converted.
     * @param rate Exchange rate of the currencies.
     * @param time Time code of the API request.
     * @param source What API was used: Host or Ayr.
     */
    public void saveConversionData(String from, String to, double rate, Map<String, Integer> time, APIType source) {
        Exchange e = findExchangeRate(from, to);

        if (e != null) {
            e.editRate(rate, time, source);
        }
        else {
            exchanges.add(new Exchange(from, to, rate, time, source));
        }       
    }

    /**
     * Saves newly fetched data from Exchange Rate Gets
     * @param answer RequestAnswer provided by the service that stored all information.
     */
    public void saveData(RequestAnswer answer) {
        Map<String, Double> ratesMap = answer.getRates();
        APIType source = answer.getApi().values().iterator().next();
        String from = answer.getCurrencyFrom();
        Map<String, Integer> time = answer.getRequestTime();

        Iterator<String> keyIt = ratesMap.keySet().iterator();
        Iterator<Double> valueIt = ratesMap.values().iterator();
       
        while (keyIt.hasNext()) {
            String to = keyIt.next();
            
            if (!from.equalsIgnoreCase(to)) {
                Double rate = valueIt.next();
                Exchange e = findExchangeRate(from, to);

                if (e != null) {
                    e.editRate(rate, time, source);
                }
                else {
                    exchanges.add(new Exchange(from, to, rate, time, source));
                }
            }
            else { // This was required due to API Ayr sending Long as the first rate in JSON instead of Double
                valueIt.next();
            }
        }
    }

    /**
     * Add single Exchange Rate to currencies.
     * @param from Currency from which it was converted.
     * @param to Currency to which it was converted.
     * @param rate Exchange rate of the currencies.
     * @param time Time code of the API request.
     * @param source What API was used: Host or Ayr.
     */
    public void addExchangeRate(String from, String to, double rate, String date, Map<String, Integer> time, APIType source) {
        Exchange e = findExchangeRate(from, to);
        if (e != null) {
            e.editRate(rate, time, source);
        }
        else {
            exchanges.add(new Exchange(from, to, rate, time, source));
        }
    }
    
    public List<Exchange> getExchanges() {
        return exchanges;
    }
}
