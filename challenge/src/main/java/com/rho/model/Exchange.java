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
    private String date;
    private boolean outdated = false;

    public Exchange(String from, String to, double rate, String date, Map<String, Integer> time, APIType source) {
        this.from = from;
        this.to = to;
        this.rate = rate;
        this.date = date;
        this.time = time;
        this.source = source;
    }

    public void editRate(double rate, String date, Map<String, Integer> time, APIType source) {
        this.rate = rate;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public double getRate() {
        return rate;
    }

    public JSONObject getExchange() {
        Map<String, Object> values = new HashMap<>();

        values.put("From", from);
        values.put("To", to);
        values.put("Rate", rate);
        values.put("Result Date", date);
        values.put("Request Time", time);
        values.put("Source", source);

        return new JSONObject(values);
    }

    @Override
    public String toString() {
        return getExchange().toJSONString();
    }
}
