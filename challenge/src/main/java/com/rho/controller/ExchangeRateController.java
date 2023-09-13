package com.rho.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rho.model.ExchangeDB;
import com.rho.model.enums.APIType;
import com.rho.model.enums.Keys;
import com.rho.model.schemas.BadRequestAnswer;
import com.rho.model.schemas.RequestAnswer;
import com.rho.services.AyrExchangeRateAPI;
import com.rho.services.Check;
import com.rho.services.HostExchangeRateAPI;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@OpenAPIDefinition(info = @Info(title = "Exchange Rate API", version = "1.0", description = "Access various exchange rates using one of two APIs: host or ayr"))
@RestController
public class ExchangeRateController {

    private ExchangeDB db = new ExchangeDB();

    private HostExchangeRateAPI hostAPI = new HostExchangeRateAPI();
    private AyrExchangeRateAPI ayrAPI = new AyrExchangeRateAPI();

    @GetMapping("/")
    public String index() {
        return "Welcome";
    }

    // ========= GET ALL RATES =========

    @Operation(summary = "Get all exchange rates for specified currency")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rates", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/rates/all/{currency}") 
    public JSONObject getAllExchangeRates(@Parameter(description = "The currency to get exchange rates from") @PathVariable String currency) {
        try {
            return hostAPI.getAllExchangeRates(db, currency);
        }
        catch (Exception e) {
            return new JSONObject(Check.fatalErrorMessage());
        }
    }

    @Operation(summary = "Get all exchange rates for specified currency with specific API")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rates", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/rates/all/{api}/{currency}")
    public JSONObject getAllExchangeRates(@Parameter(description = "The currency to get exchange rates from") @PathVariable String currency, 
                                        @Parameter(description = "Specific API to use (host or ayr)") @PathVariable String api) {
        try {
            switch (getEnumApiType(api)) {
                case HOST:
                    return hostAPI.getAllExchangeRates(db, currency);
                case AYR:
                    return ayrAPI.getAllExchangeRates(db, currency);
                default:
                    return new JSONObject(Check.unknownAPIRequested());
            } 
        }
        catch (Exception e) {
            return new JSONObject(Check.fatalErrorMessage());
        }  
    }

    // ========= GET SPECIFIC RATES =========

    @Operation(summary = "Get exchange rates for specified currency and targets")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rates", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/rates/{currency}&{targets}")
    public ResponseEntity<JSONObject> getExchangeRates(@Parameter(description = "The currency to get exchange rates from") @PathVariable String currency, 
                                                    @Parameter(description = "The currencies to get exchange rates to") @PathVariable String targets) {
        try {
            JSONObject obj = hostAPI.getExchangeRates(db, currency, targets);

            if ((boolean) obj.get(Keys.SUCCESS)) {
                return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
            }
            return new ResponseEntity<JSONObject>(obj, HttpStatus.EXPECTATION_FAILED); 
        }
        catch (Exception e) {
            return new ResponseEntity<JSONObject>(new JSONObject(Check.fatalErrorMessage()), HttpStatus.GONE);
        }
    }

    @Operation(summary = "Get exchange rates for specified currency and targets with specific API")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rates", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/rates/{api}/{currency}&{targets}")
    public JSONObject getExchangeRates(@Parameter(description = "The currency to get exchange rates from") @PathVariable String currency, 
                                    @Parameter(description = "The currencies to get exchange rates to") @PathVariable String targets, 
                                    @Parameter(description = "Specific API to use (host or ayr)") @PathVariable String api) {
        try {
            String symbols = targets.replaceAll(" ", "");
        
            switch (getEnumApiType(api)) {
                case HOST:
                    return hostAPI.getExchangeRates(db, currency, symbols);
                case AYR:
                    return ayrAPI.getExchangeRates(db, currency, symbols);
                default:
                    return new JSONObject(Check.unknownAPIRequested());
            }
        }
        catch (Exception e) {
            return new JSONObject(Check.fatalErrorMessage());
        }
    }

    // ========= CONVERT CURRENCY A TO B =========

    @Operation(summary = "Convert from one currency into another")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful conversion of currencies", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/convert/{from}&{to}&{amount}")
    public JSONObject convertCurrency(@Parameter(description = "The currency to convert from") @PathVariable String from,
                                     @Parameter(description = "The currency to convert to") @PathVariable String to, 
                                     @Parameter(description = "The amount of currency to convert") @PathVariable String amount) {
        try {
            if (!Check.isAmountNumber(amount)) {
                return new JSONObject(Check.formatWrongAmountFormatAnswer());
            }

            return hostAPI.convertCurrency(db, from, to, Double.parseDouble(amount));
        }
        catch (Exception e) {
            return new JSONObject(Check.fatalErrorMessage());
        }
    }

    @Operation(summary = "Convert from one currency into another with a specific API")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful conversion of currencies", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/convert/{api}/{from}&{to}&{amount}")
    public JSONObject convertCurrency(@Parameter(description = "The currency to convert from") @PathVariable String from, 
                                    @Parameter(description = "The currency to convert to") @PathVariable String to, 
                                    @Parameter(description = "The amount of currency to convert") @PathVariable String amount, 
                                    @Parameter(description = "Specific API to use (host or ayr)") @PathVariable String api) {
        try {
            if (!Check.isAmountNumber(amount)) {
                return new JSONObject(Check.formatWrongAmountFormatAnswer());
            }

            switch (getEnumApiType(api)) {
                case HOST:
                    return hostAPI.convertCurrency(db, from, to, Double.parseDouble(amount));
                case AYR:
                    return ayrAPI.convertCurrency(db, from, to, Double.parseDouble(amount));
                default:
                    return new JSONObject(Check.unknownAPIRequested());
            }
        }
        catch (Exception e) {
            return new JSONObject(Check.fatalErrorMessage());
        }
        
        
    }

    // ========= CONVERT CURRENCY A TO MULTIPLE =========

    @Operation(summary = "Convert from one currency into multiple")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful conversion of currencies", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @SuppressWarnings("unchecked")
    @GetMapping("/convert/multi/{from}&{targets}&{amount}")
    public JSONObject convertMultiCurrency(@Parameter(description = "The currency to convert from") @PathVariable String from, 
                                        @Parameter(description = "The currencies to convert to") @PathVariable String targets,
                                        @Parameter(description = "The amount of currency to convert") @PathVariable String amount) {
        try {
            if (!Check.isAmountNumber(amount)) {
                return new JSONObject(Check.formatWrongAmountFormatAnswer());
            }

            JSONObject obj = null;
            ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.replaceAll(" ", "").split(",")));
            Map<String, Double> currencyMap = new HashMap<>();
            Map<String, Double> ratesMap = new HashMap<>();

            for (String str : symbols) {
                obj = hostAPI.convertCurrency(db, from, str, Double.parseDouble(amount));
                currencyMap.put(str, (Double) obj.get(Keys.RESULT));
                ratesMap.put(str, (Double) obj.get(Keys.RATES));
            }
            
            if (obj != null) {
                obj.replace(Keys.RESULT, currencyMap);
                obj.replace(Keys.RATES, ratesMap);
                obj.replace(Keys.CURRENCY_TO, targets);

                return obj;
            }
            return null;
        }
        catch (Exception e) {
            return new JSONObject(Check.fatalErrorMessage());
        }
    }

    @Operation(summary = "Convert from one currency into multiple with a specific API")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful conversion of currencies", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @SuppressWarnings("unchecked")
    @GetMapping("/convert/multi/{api}/{from}&{targets}&{amount}")
    public JSONObject convertMultiCurrency(@Parameter(description = "The currency to convert from") @PathVariable String from, 
                                        @Parameter(description = "The currencies to convert to") @PathVariable String targets, 
                                        @Parameter(description = "The amount of currency to convert") @PathVariable String amount, 
                                        @Parameter(description = "Specific API to use (host or ayr)") @PathVariable String api) {
        try {
            if (!Check.isAmountNumber(amount)) {
                return new JSONObject(Check.formatWrongAmountFormatAnswer());
            }

            JSONObject obj = null;
            ArrayList<String> symbols = new ArrayList<>(Arrays.asList(targets.replaceAll(" ", "").split(",")));
            Map<String, Double> currencyMap = new HashMap<>();
            Map<String, Double> ratesMap = new HashMap<>();
            
            for (String str : symbols) {
                switch (getEnumApiType(api)) {
                    case HOST:
                        obj = hostAPI.convertCurrency(db, from, str, Double.parseDouble(amount));
                        break;
                    case AYR:
                        obj = ayrAPI.convertCurrency(db, from, str, Double.parseDouble(amount));
                        break;
                    default:
                        return new JSONObject(Check.unknownAPIRequested());
                }
                
                if (obj != null) {
                    currencyMap.put(str, (Double) obj.get(Keys.RESULT));
                    ratesMap.put(str, (Double) obj.get(Keys.RATES));
                }
            }
            
            if (obj != null) {
                obj.replace(Keys.RESULT, currencyMap);
                obj.replace(Keys.RATES, ratesMap);
                obj.replace(Keys.CURRENCY_TO, targets);

                return obj;
            }
            return null;
        }
        catch (Exception e) {
            return new JSONObject(Check.fatalErrorMessage());
        }
    }

    // ========= OTHER METHODS =========

    /**
     * Get Enum APIType from string with API code.
     * @param type Type of API identified according to their API code: HOST or AYR.
     * @return APIType equivalent of string code. Null if code is not recognized.
     */
    private APIType getEnumApiType(String type) {
        switch (type.toLowerCase().trim()) {
            case "host":
                return APIType.HOST;
            case "ayr":
                return APIType.AYR;                
            case "currency":
                return APIType.CURRENCY;
            default:
                return null;
        }
    }
}
