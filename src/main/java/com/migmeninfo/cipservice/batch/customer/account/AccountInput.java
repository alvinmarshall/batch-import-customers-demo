package com.migmeninfo.cipservice.batch.customer.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInput {
    private String correlationId;
    private String number;
    private String openingDate;
    private String accountType;
    private String holderName;
    private String openingMethod;
    private String currency;
    private String description;
    private String expectedYearlyActivityValue;

    public static Map<String, String> getAccountHeaders() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Correlation ID", "correlationId");
        headers.put("Account Number", "number");
        headers.put("Opening Date", "openingDate");
        headers.put("Account Type", "accountType");
        headers.put("Account Holder Name", "holderName");
        headers.put("Account Opening Method", "openingMethod");
        headers.put("Currency", "currency");
        headers.put("Description", "description");
        headers.put("Expected Yearly Activity Value", "expectedYearlyActivityValue");
        return headers;
    }
}
