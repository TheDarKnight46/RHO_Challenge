package com.rho.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.rho.model.ExchangeDB;
import com.rho.model.enums.APIType;
import com.rho.model.schemas.BadRequestAnswer;
import com.rho.model.schemas.CustomSchema;
import com.rho.model.schemas.RequestAnswer;
import com.rho.services.AyrExchangeRateAPI;
import com.rho.services.Check;
import com.rho.services.Connections;
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
        return "http://localhost:8080/swagger-ui/index.html#/";
    }

    // ========= GET ALL RATES =========

    @Operation(summary = "Get all exchange rates for specified currency")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rates", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/rates/all/{currency}") 
    public ResponseEntity<CustomSchema> getAllExchangeRates(@Parameter(description = "The currency to get exchange rates from") @PathVariable String currency) {
        try {
            CustomSchema obj = hostAPI.getAllExchangeRates(db, currency);
            if (obj.isSuccess()) {
                return new ResponseEntity<CustomSchema>(obj, HttpStatus.OK);
            }
            return new ResponseEntity<CustomSchema>(obj, HttpStatus.EXPECTATION_FAILED);
        }
        catch (Exception e) {
            return new ResponseEntity<CustomSchema>(Check.fatalErrorMessage(), HttpStatus.GONE);
        }
    }

    @Operation(summary = "Get all exchange rates for specified currency with specific API")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rates", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/rates/all/{api}/{currency}")
    public ResponseEntity<CustomSchema> getAllExchangeRates(@Parameter(description = "The currency to get exchange rates from") @PathVariable String currency, 
                                        @Parameter(description = "Specific API to use (host or ayr)") @PathVariable String api) {
        try {
            CustomSchema obj;
            switch (getEnumApiType(api)) {
                case HOST:
                    obj = hostAPI.getAllExchangeRates(db, currency);
                    break;
                case AYR:
                    obj = ayrAPI.getAllExchangeRates(db, currency);
                    break;
                default:
                    return new ResponseEntity<CustomSchema>(Check.unknownAPIRequested(), HttpStatus.EXPECTATION_FAILED);
            } 

            if (obj.isSuccess()) {
                return new ResponseEntity<CustomSchema>(obj, HttpStatus.OK);
            }
            return new ResponseEntity<CustomSchema>(obj, HttpStatus.EXPECTATION_FAILED);
        }
        catch (Exception e) {
            return new ResponseEntity<CustomSchema>(Check.fatalErrorMessage(), HttpStatus.GONE);
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
    public ResponseEntity<CustomSchema> getExchangeRates(@Parameter(description = "The currency to get exchange rates from") @PathVariable String currency, 
                                                    @Parameter(description = "The currencies to get exchange rates to") @PathVariable String targets) {
        try {
            CustomSchema obj = hostAPI.getExchangeRates(db, currency, targets);
            if (obj.isSuccess()) {
                return new ResponseEntity<CustomSchema>(obj, HttpStatus.OK);
            }
            return new ResponseEntity<CustomSchema>(obj, HttpStatus.EXPECTATION_FAILED); 
        }
        catch (Exception e) {
            return new ResponseEntity<CustomSchema>(Check.fatalErrorMessage(), HttpStatus.GONE);
        }
    }

    @Operation(summary = "Get exchange rates for specified currency and targets with specific API")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rates", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/rates/{api}/{currency}&{targets}")
    public ResponseEntity<CustomSchema> getExchangeRates(@Parameter(description = "The currency to get exchange rates from") @PathVariable String currency, 
                                    @Parameter(description = "The currencies to get exchange rates to") @PathVariable String targets, 
                                    @Parameter(description = "Specific API to use (host or ayr)") @PathVariable String api) {
        try {
            String symbols = targets.replaceAll(" ", "");
            CustomSchema obj;
        
            switch (getEnumApiType(api)) {
                case HOST:
                    obj = hostAPI.getExchangeRates(db, currency, symbols);
                    break;
                case AYR:
                    obj = ayrAPI.getExchangeRates(db, currency, symbols);
                    break;
                default:
                    return new ResponseEntity<CustomSchema>(Check.unknownAPIRequested(), HttpStatus.EXPECTATION_FAILED);
            }

            if (obj.isSuccess()) {
                return new ResponseEntity<CustomSchema>(obj, HttpStatus.OK);
            }
            return new ResponseEntity<CustomSchema>(obj, HttpStatus.EXPECTATION_FAILED);
        }
        catch (Exception e) {
            return new ResponseEntity<CustomSchema>(Check.fatalErrorMessage(), HttpStatus.GONE);
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
    public ResponseEntity<CustomSchema> convertCurrency(@Parameter(description = "The currency to convert from") @PathVariable String from,
                                     @Parameter(description = "The currency to convert to") @PathVariable String to, 
                                     @Parameter(description = "The amount of currency to convert") @PathVariable String amount) {
        try {
            if (!Check.isAmountNumber(amount)) {
                return new ResponseEntity<CustomSchema>(Check.formatWrongAmountFormatAnswer(), HttpStatus.EXPECTATION_FAILED);
            }
            
            CustomSchema obj = hostAPI.convertCurrency(db, from, to, Double.parseDouble(amount));
            if (obj.isSuccess()) {
                return new ResponseEntity<CustomSchema>(obj, HttpStatus.OK);
            }
            return new ResponseEntity<CustomSchema>(obj, HttpStatus.EXPECTATION_FAILED);
        }
        catch (Exception e) {
            return new ResponseEntity<CustomSchema>(Check.fatalErrorMessage(), HttpStatus.GONE);
        }
    }

    @Operation(summary = "Convert from one currency into another with a specific API")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful conversion of currencies", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/convert/{api}/{from}&{to}&{amount}")
    public ResponseEntity<CustomSchema> convertCurrency(@Parameter(description = "The currency to convert from") @PathVariable String from, 
                                    @Parameter(description = "The currency to convert to") @PathVariable String to, 
                                    @Parameter(description = "The amount of currency to convert") @PathVariable String amount, 
                                    @Parameter(description = "Specific API to use (host or ayr)") @PathVariable String api) {
        try {
            if (!Check.isAmountNumber(amount)) {
                return new ResponseEntity<CustomSchema>(Check.formatWrongAmountFormatAnswer(), HttpStatus.EXPECTATION_FAILED);
            }

            CustomSchema obj;
            switch (getEnumApiType(api)) {
                case HOST:
                    obj = hostAPI.convertCurrency(db, from, to, Double.parseDouble(amount));
                    break;
                case AYR:
                    obj = ayrAPI.convertCurrency(db, from, to, Double.parseDouble(amount));
                    break;
                default:
                    return new ResponseEntity<CustomSchema>(Check.unknownAPIRequested(), HttpStatus.EXPECTATION_FAILED);
            }

            if (obj.isSuccess()) {
                return new ResponseEntity<CustomSchema>(obj, HttpStatus.OK);
            }
            return new ResponseEntity<CustomSchema>(obj, HttpStatus.EXPECTATION_FAILED);
        }
        catch (Exception e) {
            return new ResponseEntity<CustomSchema>(Check.fatalErrorMessage(), HttpStatus.GONE);
        }
    }

    // ========= CONVERT CURRENCY A TO MULTIPLE =========

    @Operation(summary = "Convert from one currency into multiple")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful conversion of currencies", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/convert/multi/{from}&{targets}&{amount}")
    public ResponseEntity<CustomSchema> convertMultiCurrency(@Parameter(description = "The currency to convert from") @PathVariable String from, 
                                        @Parameter(description = "The currencies to convert to") @PathVariable String targets,
                                        @Parameter(description = "The amount of currency to convert") @PathVariable String amount) {
        try {
            if (!Check.isAmountNumber(amount)) {
                return new ResponseEntity<CustomSchema>(Check.formatWrongAmountFormatAnswer(), HttpStatus.EXPECTATION_FAILED);
            }

            String[] symbols = targets.replaceAll(" ", "").split(",");
            RequestAnswer answer = new RequestAnswer(true, false);
            answer.setCurrencyFrom(from);
            answer.setAmount(Double.parseDouble(amount));
            answer.addApi(APIType.HOST, "All");
            answer.setRequestTime(Connections.getCurrentTime());
            answer.setCurrencyTo(symbols);

            for (String str : symbols) {
                RequestAnswer obj = (RequestAnswer) hostAPI.convertCurrency(db, from, str, Double.parseDouble(amount));
                if (!obj.isSuccess()) {
                    return new ResponseEntity<CustomSchema>(obj, HttpStatus.EXPECTATION_FAILED);
                }

                answer.joinRates(obj.getRates());
                answer.joinResults(obj.getResults());
            }

            return new ResponseEntity<CustomSchema>(answer, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<CustomSchema>(Check.fatalErrorMessage(), HttpStatus.GONE);
        }
    }

    @Operation(summary = "Convert from one currency into multiple with a specific API")
    @ApiResponses(value= {
        @ApiResponse(responseCode = "200", description = "Successful conversion of currencies", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RequestAnswer.class)) }),
        @ApiResponse(responseCode = "417", description = "Parameter format error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
        @ApiResponse(responseCode = "410", description = "Unexpected Error", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestAnswer.class)) }),
    })
    @GetMapping("/convert/multi/{api}/{from}&{targets}&{amount}")
    public ResponseEntity<CustomSchema> convertMultiCurrency(@Parameter(description = "The currency to convert from") @PathVariable String from, 
                                        @Parameter(description = "The currencies to convert to") @PathVariable String targets, 
                                        @Parameter(description = "The amount of currency to convert") @PathVariable String amount, 
                                        @Parameter(description = "Specific API to use (host or ayr)") @PathVariable String api) {
        try {
            if (!Check.isAmountNumber(amount)) {
                return new ResponseEntity<CustomSchema>(Check.formatWrongAmountFormatAnswer(), HttpStatus.EXPECTATION_FAILED);
            }

            String[] symbols = targets.replaceAll(" ", "").split(",");
            RequestAnswer answer = new RequestAnswer(true, false);
            answer.setCurrencyFrom(from);
            answer.setAmount(Double.parseDouble(amount));
            answer.addApi(APIType.HOST, "All");
            answer.setRequestTime(Connections.getCurrentTime());
            answer.setCurrencyTo(symbols);

            for (String str : symbols) {
                RequestAnswer obj;
                switch (getEnumApiType(api)) {
                    case HOST:
                        obj = (RequestAnswer) hostAPI.convertCurrency(db, from, str, Double.parseDouble(amount));
                        break;
                    case AYR:
                        obj = (RequestAnswer) ayrAPI.convertCurrency(db, from, str, Double.parseDouble(amount));
                        break;
                    default:
                        return new ResponseEntity<CustomSchema>(Check.unknownAPIRequested(), HttpStatus.EXPECTATION_FAILED);
                }
                
                if (!obj.isSuccess()) {
                    return new ResponseEntity<CustomSchema>(obj, HttpStatus.EXPECTATION_FAILED);
                }

                answer.joinRates(obj.getRates());
                answer.joinResults(obj.getResults());
            }

            return new ResponseEntity<CustomSchema>(answer, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<CustomSchema>(Check.fatalErrorMessage(), HttpStatus.GONE);
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
            default:
                return null;
        }
    }
}
