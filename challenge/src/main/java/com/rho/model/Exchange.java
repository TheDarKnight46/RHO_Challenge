package com.rho.model;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.rho.model.enums.APIType;

public class Exchange {
    private APIType source;
    private String from, to;
    private double rate;
    private Map<String, Integer> time;
    private boolean outdated = false;

    public Exchange(String from, String to, double rate, Map<String, Integer> time, APIType source) {
        this.from = from;
        this.to = to;
        this.rate = rate;
        this.time = time;
        this.source = source;
    }

    /**
     * Edit already existing Exchange with new values.
     * @param rate New exchange rate.
     * @param date New date.
     * @param time New request time.
     * @param source New API source.
     */
    public void editRate(double rate, Map<String, Integer> time, APIType source) {
        this.rate = rate;
        this.time = time;
        this.source = source;
        this.outdated = false;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Map<String, Integer> getRequestTime() {
        return time;
    }

    public APIType getSource() {
        return source;
    }

    public void setOutdated(boolean flag) {
        outdated = flag;
    }

    public boolean getOutdated() {
        return outdated;
    }

    public double getRate() {
        return rate;
    }

    /**
     * Format the main information of the Exchange object and return it.
     * @return JSONObject with the main information of the Exchange object.
     */
    public JSONObject getExchange() {
        Map<String, Object> values = new HashMap<>();

        values.put("From", from);
        values.put("To", to);
        values.put("Rate", rate);
        values.put("Request Time", time);
        values.put("Source", source);

        return new JSONObject(values);
    }
}
