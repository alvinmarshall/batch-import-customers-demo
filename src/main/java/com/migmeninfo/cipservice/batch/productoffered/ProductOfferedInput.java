package com.migmeninfo.cipservice.batch.productoffered;

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
public class ProductOfferedInput {
    private String correlationId;
    private String product;

    public static Map<String, String> getProductOfferedHeaders() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Correlation ID", "correlationId");
        headers.put("Product", "product");
        return headers;
    }
}
