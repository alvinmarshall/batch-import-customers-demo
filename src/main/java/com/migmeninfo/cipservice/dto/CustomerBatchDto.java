package com.migmeninfo.cipservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerBatchDto implements Serializable {
    private boolean deferred;

    @JsonProperty("initiated_by")
    private String initialedBy;

    @JsonProperty("batch_files")
    @Builder.Default
    private Set<DocumentDto> batchFiles = new HashSet<>();
}
