package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migmeninfo.cipservice.common.DocumentType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "documents")
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class Document extends BaseEntity implements Serializable {
    @Column(name = "document_key")
    private String key;
    private String bucket;
    private String url;
    private long size;
    private String mime;

    @Column(name = "file_name")
    @JsonProperty("file_name")
    private String fileName;

    @Column(name = "document_type")
    @Enumerated(EnumType.STRING)
    @JsonProperty("document_type")
    private DocumentType documentType;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @JsonIgnore
    @ToString.Exclude
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "beneficial_owner_id", referencedColumnName = "id")
    @JsonIgnore
    @ToString.Exclude
    private BeneficialOwner beneficialOwner;
}
