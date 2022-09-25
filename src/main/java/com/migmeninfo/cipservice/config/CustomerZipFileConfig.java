package com.migmeninfo.cipservice.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "customer-zip-filename")
@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerZipFileConfig {
    private String individual;
    private String organization;
    private String address;
    private String businessUnit;
    private String account;
    private String document;
    private String productOffered;
    private String relationship;
    private String marketServed;
    private String countryOfOperation;
    private String beneficialOwner;
}
