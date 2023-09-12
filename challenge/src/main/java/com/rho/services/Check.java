package com.rho.services;

import java.util.HashMap;
import java.util.Map;

import com.rho.model.enums.Keys;

public abstract class Check {
    
    public static boolean isCurrencyCode(String currency) {
        try {
            int i = Integer.parseInt(currency);
            return false;
        } 
        catch (NumberFormatException e) {
            return currency.length() == 3 ? true : false;
        }
    }

    public static Map<Keys, Object> formatWrongCurrencyFormatAnswer() {
        Map<Keys, Object> map = new HashMap<>();

        map.put(Keys.SUCCESS, false);
        map.put(Keys.CALL_EXECUTED, false);
        map.put(Keys.ERROR_MESSAGE, "Wrong currency format");

        return map;
    }

    public static boolean isAmountNumber(String amount) {
        try {
            double num = Double.parseDouble(amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Map<Keys, Object> formatWrongAmountFormatAnswer() {
        Map<Keys, Object> map = new HashMap<>();

        map.put(Keys.SUCCESS, false);
        map.put(Keys.CALL_EXECUTED, false);
        map.put(Keys.ERROR_MESSAGE, "Wrong amount number format");

        return map;
    }
}
