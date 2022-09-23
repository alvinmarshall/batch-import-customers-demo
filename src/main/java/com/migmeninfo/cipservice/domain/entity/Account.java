package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.migmeninfo.cipservice.common.CustomerType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "accounts")
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class Account extends BaseEntity implements Serializable {
    private boolean closed;
    private BigDecimal balance;
    private String branch;
    private boolean anonymous;
    private String description;

    @JsonProperty("account_currency")
    private String currency;

    @Column(name = "customer_type")
    @Enumerated(EnumType.STRING)
    @JsonProperty("customer_type")
    private CustomerType customerType;

    @Column(name = "opening_method")
    @JsonProperty("account_opening_method")
    private String openingMethod;

    @JsonProperty("account_number")
    private String number;

    @Column(name = "account_value")
    @JsonProperty("account_value")
    private String value;

    @Column(name = "account_class")
    @JsonProperty("account_class")
    private String accountClass;

    @Column(name = "opening_date")
    @JsonProperty("opening_date")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "MM-dd-yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate openingDate;

    @Column(name = "holder_name")
    @JsonProperty("account_holder")
    private String holderName;

    @Column(name = "closing_date")
    @JsonProperty("closing_date")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "MM-dd-yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate closingDate;

    @Column(name = "expected_value")
    @JsonProperty("expected_account_value")
    private BigDecimal expectedValue;

    @Column(name = "expected_monthly_activity_value")
    @JsonProperty("expected_monthly_activity_value")
    private BigDecimal expectedMonthlyActivityValue;

    @Column(name = "expected_yearly_activity_value")
    @JsonProperty("expected_yearly_activity_value")
    private BigDecimal expectedYearlyActivityValue;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @JsonIgnore
    @ToString.Exclude
    private Customer customer;

}
