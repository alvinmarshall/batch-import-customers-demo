package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@Data
public class BusinessUnit implements Serializable {
    @JsonProperty("business_unit_id")
    private String businessUnitId;

    @JsonProperty("business_unit")
    private String businessUnit;

    @Column(name = "is_default")
    @JsonProperty("default")
    private boolean isDefault;
}
