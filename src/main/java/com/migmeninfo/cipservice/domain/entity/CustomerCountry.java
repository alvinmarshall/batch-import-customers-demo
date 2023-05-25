package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "customer_countries")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CustomerCountry extends BaseEntity implements Serializable {
    private String name;

    @Column(name = "iso_2_code")
    @JsonProperty("iso_2_code")
    private String iso2Code;

    @Column(name = "iso_3_code")
    @JsonProperty("iso_3_code")
    private String iso3Code;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Customer customer;
}
