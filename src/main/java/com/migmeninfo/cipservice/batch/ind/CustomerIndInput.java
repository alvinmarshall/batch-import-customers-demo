package com.migmeninfo.cipservice.batch.ind;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migmeninfo.cipservice.common.CustomerType;
import com.migmeninfo.cipservice.common.TinType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerIndInput {
    private String correlationId;
    private String accountOpeningDate;
    private String firstName;
    private String middleName;
    private String lastName;
    private String citizenship;
    private String maritalStatus;
    private String email;
    private String phoneNumber;
    private String dob;
    private String ssn;
    private String sourceOfWealth;
    private String taxIdentificationNumber;
    private String countryOfSecondaryCitizenship;
    private String countryOfResidence;
    private String occupation;
    private String annualIncome;
    private String gender;
    private String countryOfTaxation;
    private CustomerType customerType = CustomerType.INDIVIDUAL;

    @JsonIgnore
    public TinType getTinType() {
        if (!ObjectUtils.isEmpty(ssn)) return TinType.SSN;
        if (!ObjectUtils.isEmpty(taxIdentificationNumber)) return TinType.ITIN;
        return TinType.UNKNOWN;
    }

    @JsonIgnore
    public static Map<String, String> getCustomerHeaders() {
        Map<String, String> customerHeaders = new LinkedHashMap<>();
        customerHeaders.put("Correlation ID", "correlationId");
        customerHeaders.put(" Account Opening Date", "accountOpeningDate");
        customerHeaders.put("First Name", "firstName");
        customerHeaders.put("Middle Name", "middleName");
        customerHeaders.put("Last Name", "lastName");
        customerHeaders.put("Country of Citizenship", "citizenship");
        customerHeaders.put("Marital Status", "maritalStatus");
        customerHeaders.put("Email", "email");
        customerHeaders.put("Phone Number", "phoneNumber");
        customerHeaders.put("Date of Birth", "dob");
        customerHeaders.put(" Social Security Number", "ssn");
        customerHeaders.put("Source of Wealth", "sourceOfWealth");
        customerHeaders.put("Tax Identification Number", "taxIdentificationNumber");
        customerHeaders.put("Country of Secondary Citizenship", "countryOfSecondaryCitizenship");
        customerHeaders.put("Country of Residence", "countryOfResidence");
        customerHeaders.put("Occupation", "occupation");
        customerHeaders.put("Annual Income", "annualIncome");
        customerHeaders.put("Gender", "gender");
        customerHeaders.put("Country of Taxation", "countryOfTaxation");
        return customerHeaders;
    }

}
