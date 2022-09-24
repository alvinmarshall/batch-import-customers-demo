package com.migmeninfo.cipservice.batch.marketserved;

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
public class MarketServedInput {
    private String correlationId;
    private String market;

    public static Map<String, String> getMarketServedHeaders() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Correlation ID", "correlationId");
        headers.put("Market", "market");
        return headers;
    }
}
