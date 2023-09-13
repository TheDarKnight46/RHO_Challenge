package com.rho.services;

import com.rho.model.schemas.BadRequestAnswer;

public abstract class Check {
    
    public static boolean isCurrencyCode(String currency) {
        try {
            Integer.parseInt(currency);
            return false;
        } 
        catch (NumberFormatException e) {
            return currency.length() == 3 ? true : false;
        }
    }

    public static BadRequestAnswer formatWrongCurrencyFormatAnswer() {
        return new BadRequestAnswer(false, false, "Wrong currency format");
    }

    public static boolean isAmountNumber(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static BadRequestAnswer formatWrongAmountFormatAnswer() {
        return new BadRequestAnswer(false, false, "Wrong amount number format");
    }

    public static BadRequestAnswer fatalErrorMessage() {
        return new BadRequestAnswer(false, false, "Fatal error occurred");
    }

    public static BadRequestAnswer unknownAPIRequested() {
        return new BadRequestAnswer(false, false, "Unknown API Requested");
    }
}
