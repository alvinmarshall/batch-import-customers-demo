package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "customer_market_served")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MarketServed extends BaseEntity implements Serializable {
    private String market;

    @Column(name = "market_code")
    @JsonProperty("market_code")
    private String marketCode;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Customer customer;
}
