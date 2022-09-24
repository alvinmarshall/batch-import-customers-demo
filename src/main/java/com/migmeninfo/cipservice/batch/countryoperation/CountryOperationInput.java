package com.migmeninfo.cipservice.batch.countryoperation;

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
public class CountryOperationInput {
    private String correlationId;
    private String country;

    public static Map<String, String> getCountryOperationHeaders() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Correlation ID", "correlationId");
        headers.put("Country", "country");
        return headers;
    }
}
