package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "products_offered")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductsOffered extends BaseEntity implements Serializable {
    private String name;

    @Column(name = "product_code")
    @JsonProperty("product_code")
    private String productCode;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Customer customer;
}
