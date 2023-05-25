package com.migmeninfo.cipservice.batch.customer.address;

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
public class AddressInput {
    private String correlationId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private String country;

    public static Map<String, String> getAddressHeaders() {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Correlation ID", "correlationId");
        headers.put("Address 1", "addressLine1");
        headers.put("Address 2", "addressLine2");
        headers.put("City", "city");
        headers.put("State", "state");
        headers.put("Zip", "zip");
        headers.put("Country", "country");
        return headers;
    }
}
