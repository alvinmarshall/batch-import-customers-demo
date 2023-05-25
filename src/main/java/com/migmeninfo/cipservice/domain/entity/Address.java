package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "addresses")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Address extends BaseEntity implements Serializable {
    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Column(name = "address_line_1")
    @JsonProperty("address_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    @JsonProperty("address_2")
    private String addressLine2;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @JsonIgnore
    @ToString.Exclude
    private Customer customer;
}
