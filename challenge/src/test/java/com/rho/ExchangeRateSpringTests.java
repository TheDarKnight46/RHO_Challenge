package com.rho;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.rho.model.ExchangeDB;
import com.rho.model.enums.APIType;
import com.rho.model.schemas.BadRequestAnswer;
import com.rho.model.schemas.RequestAnswer;
import com.rho.services.AyrExchangeRateAPI;
import com.rho.services.Connections;
import com.rho.services.HostExchangeRateAPI;

@SpringBootTest
public class ExchangeRateSpringTests {
    
    // ========= GET ALL RATES =========

    @Test
    public void testGetAllRatesHost() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String currency = "EUR";

        RequestAnswer obj = (RequestAnswer) api.getAllExchangeRates(db, currency);

        assertEquals(obj.getCurrencyFrom(), currency);
        assertTrue(obj.isCallExecuted());
        assertTrue(obj.isSuccess());
        assertEquals(obj.getApi().values().iterator().next(), APIType.HOST);
    }

    @Test
    public void testGetAllRatesAyr() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String currency = "USD";

        RequestAnswer obj = (RequestAnswer) api.getAllExchangeRates(db, currency);

        assertEquals(obj.getCurrencyFrom(), currency);
        assertTrue(obj.isCallExecuted());
        assertTrue(obj.isSuccess());
        assertEquals(obj.getApi().values().iterator().next(), APIType.AYR);
    }

    @Test
    public void testGetAllRatesMalformedParams() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String currency = "US";

        BadRequestAnswer obj = (BadRequestAnswer) api.getAllExchangeRates(db, currency);

        assertFalse(obj.isSuccess());
        assertFalse(obj.isCallExecuted());
    }

    // ========= GET SPECIFIC RATES =========

    @Test
    public void testSpecificRatesHost() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String currency = "EUR";
        String targets = "USD,GBP,CHF";

        RequestAnswer obj = (RequestAnswer) api.getExchangeRates(db, currency, targets);

        assertTrue(obj.isCallExecuted());
        assertTrue(obj.isSuccess());
        assertEquals(currency, obj.getCurrencyFrom());
        assertEquals(obj.getApi().values().iterator().next(), APIType.HOST);

        Map<String, Double> rates = obj.getRates();

        assertTrue(rates.containsKey("USD"));
        assertTrue(rates.containsKey("CHF"));
        assertTrue(rates.containsKey("GBP"));
    }

    @Test
    public void testSpecificRatesAyr() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String currency = "CHF";
        String targets = "USD,GBP,CAD";

        RequestAnswer obj = (RequestAnswer) api.getExchangeRates(db, currency, targets);

        assertTrue(obj.isCallExecuted());
        assertTrue(obj.isSuccess());
        assertEquals(currency, obj.getCurrencyFrom());
        assertEquals(obj.getApi().values().iterator().next(), APIType.AYR);

        Map<String, Double> rates = obj.getRates();

        assertTrue(rates.containsKey("USD"));
        assertTrue(rates.containsKey("CAD"));
        assertTrue(rates.containsKey("GBP"));
    }

    @Test
    public void testSpecificRatesMalformedParams() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String currency = "EU";
        String targets = "USD,GBP,CAD";

        BadRequestAnswer obj = (BadRequestAnswer) api.getExchangeRates(db, currency, targets);

        assertFalse(obj.isSuccess());
        assertFalse(obj.isCallExecuted());
    }

    // ========= CONVERT CURRENCY A TO B =========

    @Test
    public void testConvertHost() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String from = "EUR";
        String to = "JPY";
        double amount = 1523.0;

        RequestAnswer obj = (RequestAnswer) api.convertCurrency(db, from, to, amount);

        assertTrue(obj.isCallExecuted());
        assertTrue(obj.isSuccess());
        assertEquals(from, obj.getCurrencyFrom());
        assertEquals(to, obj.getCurrencyTo().get(0));
        assertEquals(240011, obj.getResults().values().iterator().next(), 1000);
    }

    @Test
    public void testConvertAyr() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String from = "EUR";
        String to = "USD";
        double amount = 589.0;

        RequestAnswer obj = (RequestAnswer) api.convertCurrency(db, from, to, amount);

        assertTrue(obj.isCallExecuted());
        assertTrue(obj.isSuccess());
        assertEquals(from, obj.getCurrencyFrom());
        assertEquals(to, obj.getCurrencyTo().get(0));
        assertEquals(632.5, obj.getResults().values().iterator().next(), 100);
    }

    @Test
    public void testConvertMalformedParams() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String from = "ER";
        String to = "JPY";
        double amount = 1523.0;

        BadRequestAnswer obj = (BadRequestAnswer) api.convertCurrency(db, from, to, amount);

        assertFalse(obj.isSuccess());
        assertFalse(obj.isCallExecuted());
    }

    // ========= CONVERT CURRENCY A TO MULTIPLE =========

    @Test
    public void testConvertMultiHost() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String from = "EUR";
        String to = "USD,GBP,JPY";
        double amount = 756.0;
        
        RequestAnswer answer = new RequestAnswer(true, true);

        for (String symbol : to.split(",")) {
            RequestAnswer obj = (RequestAnswer) api.convertCurrency(db, from, symbol, amount);
            if (obj.isSuccess() != true) {
                answer.setSuccess(false);
            }

            answer.addCurrencyTo(symbol);
            answer.joinRates(obj.getRates());
            answer.joinResults(obj.getResults());
        }

        assertTrue(answer.isSuccess());
        assertTrue(answer.isCallExecuted());
        assertTrue(answer.getRates().containsKey("USD"));
        assertTrue(answer.getRates().containsKey("GBP"));
        assertTrue(answer.getRates().containsKey("JPY"));
    }   
    
    @Test
    public void testConvertMultiAyr() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String from = "EUR";
        String to = "USD,GBP,JPY";
        int amount = 756;
        
        RequestAnswer answer = new RequestAnswer(true, true);

        for (String symbol : to.split(",")) {
            RequestAnswer obj = (RequestAnswer) api.convertCurrency(db, from, symbol, amount);
            if (obj.isSuccess() != true) {
                answer.setSuccess(false);
            }

            answer.addCurrencyTo(symbol);
            answer.joinRates(obj.getRates());
            answer.joinResults(obj.getResults());
        }

        assertTrue(answer.isSuccess());
        assertTrue(answer.isCallExecuted());
        assertTrue(answer.getRates().containsKey("USD"));
        assertTrue(answer.getRates().containsKey("GBP"));
        assertTrue(answer.getRates().containsKey("JPY"));
    }

    @Test
    public void testConvertMultiMalformedParams() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String from = "EU";
        String to = "USD,GBP,JPY";
        int amount = 756;
        
        BadRequestAnswer obj = (BadRequestAnswer) api.convertCurrency(db, from, to, amount);;

        assertFalse(obj.isSuccess());
        assertFalse(obj.isCallExecuted());
    } 

    // ========= OTHER =========

    @Test
    public void testReduceCallMechanismTrue() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
		Map<String, Integer> time = Connections.getCurrentTime();
		
        db.addExchangeRate("EUR", "USD", 1.074362, "2023-09-12", time, APIType.HOST);
        db.addExchangeRate("EUR", "GBP", 0.85876, "2023-09-12", time, APIType.HOST);
        db.addExchangeRate("EUR", "CHF", 0.957468, "2023-09-12", time, APIType.HOST);

        RequestAnswer obj = (RequestAnswer) api.getExchangeRates(db, "EUR", "USD,GBP,CHF");

        assertTrue(obj.isSuccess());
        assertFalse(obj.isCallExecuted());
    }

    @Test
    public void testReduceCallMechanismAfterSave() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
		Map<String, Integer> time = Connections.getCurrentTime();

        db.addExchangeRate("EUR", "GBP", 0.85876, "2023-09-12", time, APIType.HOST);
        db.addExchangeRate("EUR", "CHF", 0.957468, "2023-09-12", time, APIType.HOST);

        RequestAnswer obj1 = (RequestAnswer) api.getExchangeRates(db, "EUR", "USD");

        assertTrue(obj1.isSuccess());
        assertTrue(obj1.isCallExecuted());

        RequestAnswer obj2 = (RequestAnswer) api.getExchangeRates(db, "EUR", "USD,GBP,CHF");

        assertTrue(obj2.isSuccess());
        assertFalse(obj2.isCallExecuted());
    }

    @Test
    public void testReduceCallMechanismFalse() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
		Map<String, Integer> time = Connections.getCurrentTime();
		
        db.addExchangeRate("EUR", "GBP", 1.074362, "2023-09-12", time, APIType.HOST);
        db.addExchangeRate("EUR", "CHF", 1.074362, "2023-09-12", time, APIType.HOST);

        RequestAnswer obj = (RequestAnswer) api.getExchangeRates(db, "EUR", "USD,GBP,CHF");

        assertTrue(obj.isSuccess());
        assertTrue(obj.isCallExecuted());
    }

    @Test
    public void testReduceCallMechanismWait() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        LocalTime now = LocalTime.now();
		Map<String, Integer> time = new HashMap<>();
		
		time.put("Hour", now.getHour());
		time.put("Minute", now.getMinute()-2);
		time.put("Seconds", now.getSecond());

        db.addExchangeRate("EUR", "USD", 1.074362, "2023-09-12", time, APIType.HOST);
        db.addExchangeRate("EUR", "GBP", 0.85876, "2023-09-12", time, APIType.HOST);
        db.addExchangeRate("EUR", "CHF", 0.957468, "2023-09-12", time, APIType.HOST);

        RequestAnswer obj = (RequestAnswer) api.getExchangeRates(db, "EUR", "USD,GBP,CHF");

        assertTrue(obj.isSuccess());
        assertTrue(obj.isCallExecuted());
    }

}
