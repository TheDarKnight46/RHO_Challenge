package com.rho.data;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class Exchange {
    private String from, to;
    private float rate;
    private String date;

    public Exchange(String from, String to, float rate, String date) {
        this.from = from;
        this.to = to;
        this.rate = rate;
        this.date = date;
    }

    public void editRate(float rate, String date) {
        this.rate = rate;
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public JSONObject getRate() {
        Map<String, Object> values = new HashMap<>();

        values.put("from", from);
        values.put("to", to);
        values.put("rate", rate);
        values.put("date", date);

        return new JSONObject(values);
    }
}
