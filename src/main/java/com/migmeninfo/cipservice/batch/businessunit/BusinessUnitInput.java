package com.migmeninfo.cipservice.batch.businessunit;

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
public class BusinessUnitInput {
    private String correlationId;
    private String name;

    public static Map<String, String> getBusinessUnitHeaders() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Correlation ID", "correlationId");
        headers.put("Business Unit", "name");
        return headers;
    }
}
