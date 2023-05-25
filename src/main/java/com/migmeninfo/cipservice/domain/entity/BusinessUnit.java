package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessUnit implements Serializable {
    @JsonProperty("business_unit_id")
    private String businessUnitId;

    @JsonProperty("business_unit")
    private String businessUnit;

    private String correlationId;

    @Column(name = "is_default")
    @JsonProperty("default")
    private boolean isDefault;
}
