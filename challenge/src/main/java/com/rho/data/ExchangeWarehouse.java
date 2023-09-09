package com.rho.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public abstract class ExchangeWarehouse {
    public static List<Exchange> conversions = new ArrayList<>();

    public static Exchange findExchangeRate(String from, String to) {
        for (Exchange conv : conversions) {
            if (conv.getFrom().equals(from) && conv.getTo().equals(to)) {
                return conv;
            }
        }
        return null;
    } 

    public static void addExchangeRate(String from, String to, float rate, String date) {
        Exchange conv = findExchangeRate(from, to);
        if (conv != null) {
            conv.editRate(rate, date);
        }
        else {
            conversions.add(conv);
        }
    }

    public static JSONObject getExchangeRate(String from, String to) {
        Exchange conv = findExchangeRate(from, to);
        return conv.getRate();
    }    
}
