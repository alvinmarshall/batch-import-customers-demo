package com.migmeninfo.cipservice.batch.customer.org;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migmeninfo.cipservice.common.TinType;
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
public class CustomerOrgInput {
    private String correlationId;
    private String accountOpeningDate;
    private String customerType;
    private String name;
    private String parentCompany;
    private String doingBusinessAs;
    private String countryOfHeadQuarters;
    private String dateOfIncorporation;
    private String industry;
    private String ownership;
    private String legalStructure;
    private String phoneNumber;
    private String email;
    private String taxIdentificationNumber;
    private TinType tinType = TinType.ITIN;

    @JsonIgnore
    public static Map<String, String> getCustomerHeaders() {
        Map<String, String> customerHeaders = new LinkedHashMap<>();
        customerHeaders.put("Correlation ID", "correlationId");
        customerHeaders.put("Account Opening Date", "accountOpeningDate");
        customerHeaders.put("Customer Type", "customerType");
        customerHeaders.put("Name", "name");
        customerHeaders.put("Parent Company", "parentCompany");
        customerHeaders.put("Doing Business As", "doingBusinessAs");
        customerHeaders.put("Country of Headquarters", "countryOfHeadQuarters");
        customerHeaders.put("Date of Incorporation", "dateOfIncorporation");
        customerHeaders.put("Industry", "industry");
        customerHeaders.put("Ownership", "ownership");
        customerHeaders.put("Legal Structure", "legalStructure");
        customerHeaders.put("Phone Number", "phoneNumber");
        customerHeaders.put("Email", "email");
        customerHeaders.put("Tax Identification Number", "taxIdentificationNumber");
        return customerHeaders;
    }

}
