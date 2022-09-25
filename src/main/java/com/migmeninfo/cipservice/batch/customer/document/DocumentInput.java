package com.migmeninfo.cipservice.batch.customer.document;

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
public class DocumentInput {
    private String correlationId;
    private String documentType;
    private String url;

    public static Map<String, String> getDocumentHeaders() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Correlation ID", "correlationId");
        headers.put("Document Type", "documentType");
        headers.put(" URL", "url");
        return headers;
    }
}
