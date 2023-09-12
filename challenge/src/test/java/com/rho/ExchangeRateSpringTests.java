package com.rho;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.rho.model.ExchangeDB;
import com.rho.model.enums.APIType;
import com.rho.model.enums.Keys;
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

        JSONObject obj = api.getAllExchangeRates(db, currency);

        assertEquals((String) obj.get(Keys.CURRENCY_FROM), currency);
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertEquals((APIType) obj.get(Keys.API), APIType.HOST);
    }

    @Test
    public void testGetAllRatesAyr() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String currency = "USD";

        JSONObject obj = api.getAllExchangeRates(db, currency);

        assertEquals((String) obj.get(Keys.CURRENCY_FROM), currency);
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertEquals((APIType) obj.get(Keys.API), APIType.AYR);
    }

    @Test
    public void testGetAllRatesMalformedParams() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String currency = "US";

        JSONObject obj = api.getAllExchangeRates(db, currency);

        assertFalse((boolean) obj.get(Keys.SUCCESS));
        assertFalse((boolean) obj.get(Keys.CALL_EXECUTED));
    }

    // ========= GET SPECIFIC RATES =========

    @Test
    @SuppressWarnings("unchecked")
    public void testSpecificRatesHost() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String currency = "EUR";
        String targets = "USD,GBP,CHF";

        JSONObject obj = api.getExchangeRates(db, currency, targets);

        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertEquals(currency, (String) obj.get(Keys.CURRENCY_FROM));

        Map<String, Double> rates = (Map<String, Double>) obj.get(Keys.RATES);

        assertTrue(rates.containsKey("USD"));
        assertTrue(rates.containsKey("CHF"));
        assertTrue(rates.containsKey("GBP"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSpecificRatesAyr() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String currency = "CHF";
        String targets = "USD,GBP,CAD";

        JSONObject obj = api.getExchangeRates(db, currency, targets);

        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertEquals(currency, (String) obj.get(Keys.CURRENCY_FROM));

        Map<String, Double> rates = (Map<String, Double>) obj.get(Keys.RATES);

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

        JSONObject obj = api.getExchangeRates(db, currency, targets);

        assertFalse((boolean) obj.get(Keys.SUCCESS));
        assertFalse((boolean) obj.get(Keys.CALL_EXECUTED));
    }

    // ========= CONVERT CURRENCY A TO B =========

    @Test
    public void testConvertHost() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String from = "EUR";
        String to = "JPY";
        double amount = 1523.0;

        JSONObject obj = api.convertCurrency(db, from, to, amount);

        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertEquals(from, (String) obj.get(Keys.CURRENCY_FROM));
        assertEquals(to, (String) obj.get(Keys.CURRENCY_TO));
        assertEquals(240011, (double) obj.get(Keys.RESULT), 1000);
    }

    @Test
    public void testConvertAyr() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String from = "EUR";
        String to = "USD";
        double amount = 589.0;

        JSONObject obj = api.convertCurrency(db, from, to, amount);

        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertEquals(from, (String) obj.get(Keys.CURRENCY_FROM));
        assertEquals(to, (String) obj.get(Keys.CURRENCY_TO));
        assertEquals(632.5, (double) obj.get(Keys.RESULT), 100);
    }

    @Test
    public void testConvertMalformedParams() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String from = "EUR";
        String to = "JPY";
        double amount = 1523.0;

        JSONObject obj = api.convertCurrency(db, from, to, amount);

        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertEquals(from, (String) obj.get(Keys.CURRENCY_FROM));
        assertEquals(to, (String) obj.get(Keys.CURRENCY_TO));
        assertEquals(240011, (double) obj.get(Keys.RESULT), 1000);
    }

    // ========= CONVERT CURRENCY A TO MULTIPLE =========

    @Test
    public void testConvertMultiHost() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
        String from = "EUR";
        String to = "USD,GBP,JPY";
        double amount = 756.0;
        
        JSONObject obj = null;
        Map<String, Double> currencyMap = new HashMap<>();
        Map<String, Double> ratesMap = new HashMap<>();

        for (String symbol : to.split(",")) {
            obj = api.convertCurrency(db, from, symbol, amount);

            if (obj != null) {
                currencyMap.put(symbol, (Double) obj.get(Keys.RESULT));
                ratesMap.put(symbol, (Double) obj.get(Keys.RATES));
            }
        }

        assertTrue(obj != null);
        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertTrue((boolean) currencyMap.containsKey("USD"));
        assertTrue((boolean) currencyMap.containsKey("GBP"));
        assertTrue((boolean) currencyMap.containsKey("JPY"));
    }   
    
    @Test
    public void testConvertMultiAyr() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String from = "EUR";
        String to = "USD,GBP,JPY";
        int amount = 756;
        
        JSONObject obj = null;
        Map<String, Double> currencyMap = new HashMap<>();
        Map<String, Double> ratesMap = new HashMap<>();

        for (String symbol : to.split(",")) {
            obj = api.convertCurrency(db, from, symbol, amount);

            if (obj != null) {
                currencyMap.put(symbol, (Double) obj.get(Keys.RESULT));
                ratesMap.put(symbol, (Double) obj.get(Keys.RATES));
            }
        }

        assertTrue(obj != null);
        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
        assertTrue((boolean) currencyMap.containsKey("USD"));
        assertTrue((boolean) currencyMap.containsKey("GBP"));
        assertTrue((boolean) currencyMap.containsKey("JPY"));
    }

    @Test
    public void testConvertMultiMalformedParams() {
        ExchangeDB db = new ExchangeDB();
        AyrExchangeRateAPI api = new AyrExchangeRateAPI();
        String from = "EU";
        String to = "USD,GBP,JPY";
        int amount = 756;
        
        JSONObject obj = api.convertCurrency(db, from, to, amount);;

        assertTrue(obj != null);
        assertFalse((boolean) obj.get(Keys.SUCCESS));
        assertFalse((boolean) obj.get(Keys.CALL_EXECUTED));
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

        JSONObject obj = api.getExchangeRates(db, "EUR", "USD,GBP,CHF");

        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertFalse((boolean) obj.get(Keys.CALL_EXECUTED));
    }

    @Test
    public void testReduceCallMechanismAfterSave() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
		Map<String, Integer> time = Connections.getCurrentTime();

        db.addExchangeRate("EUR", "GBP", 0.85876, "2023-09-12", time, APIType.HOST);
        db.addExchangeRate("EUR", "CHF", 0.957468, "2023-09-12", time, APIType.HOST);

        JSONObject obj1 = api.getExchangeRates(db, "EUR", "USD");

        assertTrue((boolean) obj1.get(Keys.SUCCESS));
        assertTrue((boolean) obj1.get(Keys.CALL_EXECUTED));

        JSONObject obj2 = api.getExchangeRates(db, "EUR", "USD,GBP,CHF");

        assertTrue((boolean) obj2.get(Keys.SUCCESS));
        assertFalse((boolean) obj2.get(Keys.CALL_EXECUTED));
    }

    @Test
    public void testReduceCallMechanismFalse() {
        ExchangeDB db = new ExchangeDB();
        HostExchangeRateAPI api = new HostExchangeRateAPI();
		Map<String, Integer> time = Connections.getCurrentTime();
		
        db.addExchangeRate("EUR", "GBP", 1.074362, "2023-09-12", time, APIType.HOST);
        db.addExchangeRate("EUR", "CHF", 1.074362, "2023-09-12", time, APIType.HOST);

        JSONObject obj = api.getExchangeRates(db, "EUR", "USD,GBP,CHF");

        assertTrue((boolean) obj.get(Keys.SUCCESS));
        assertTrue((boolean) obj.get(Keys.CALL_EXECUTED));
    }

}
