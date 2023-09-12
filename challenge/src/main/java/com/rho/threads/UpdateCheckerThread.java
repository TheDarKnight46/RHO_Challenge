package com.rho.threads;

import java.time.LocalTime;
import java.util.Map;

import com.rho.model.Exchange;
import com.rho.model.ExchangeDB;

public class UpdateCheckerThread extends Thread {
    private boolean end;
    private ExchangeDB db;

    public UpdateCheckerThread(ExchangeDB db) {
        super();
        this.db = db;
        this.end = false;     
    }

    public void run() {
        do {
            for (Exchange exchange : db.getExchanges()) {
                System.out.println(exchange.toString()); //debug only

                if (checkRequiresUpdate(exchange.getRequestTime())) {
                    exchange.setOutdated(true);
                }
                else {
                    exchange.setOutdated(false);
                }
            }
        } while (!end);
    }

    public void stopThread() {
        end = true;
    }

    private boolean checkRequiresUpdate(Map<String, Integer> time) {
        LocalTime now = LocalTime.now();
        int hour, min;

        hour = now.getHour();
        min = now.getMinute();

        if ((int) time.get("hour") > hour || (int) time.get("hour") < hour) {
            return true;
        }
        else {
            int dif = min - (int) time.get("min");
            if (dif >= 1) {
                return true;
            }
            else {
                return false;
            }
        }
    }
    
}
