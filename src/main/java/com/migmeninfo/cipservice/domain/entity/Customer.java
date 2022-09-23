package com.migmeninfo.cipservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.migmeninfo.cipservice.common.CustomerType;
import com.migmeninfo.cipservice.common.Gender;
import com.migmeninfo.cipservice.common.MaritalStatus;
import com.migmeninfo.cipservice.common.TinType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Table(name = "customers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@TypeDef(name = "json", typeClass = JsonType.class)
public class Customer extends BaseEntity implements Serializable {
    private String ownership;
    private String citizenship;
    private String email;
    private String occupation;
    private String industry;

    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Column(name = "middle_name")
    @JsonProperty("middle_name")
    private String middleName;

    @Column(name = "correlation_id")
    @JsonProperty("correlation_id")
    private String correlationId;

    @Column(name = "customer_type")
    @JsonProperty("customer_type")
    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @JsonProperty("business_units")
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Set<BusinessUnit> businessUnits = new HashSet<>();

    @JsonProperty("date_of_birth")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate dob;


    @Column(name = "marital_status")
    @JsonProperty("marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "phone_number")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Column(name = "source_of_wealth")
    @JsonProperty("source_of_wealth")
    private String sourceOfWealth;

    @Column(name = "tax_identification_number")
    @JsonProperty("tax_identification_number")
    private String taxIdentificationNumber;

    @Column(name = "tin_type")
    @JsonProperty("tin_type")
    @Enumerated(EnumType.STRING)
    private TinType tinType;

    @Column(name = "country_of_secondary_citizenship")
    @JsonProperty("country_of_secondary_citizenship")
    private String countryOfSecondaryCitizenship;

    @Column(name = "country_of_residence")
    @JsonProperty("country_of_residence")
    private String countryOfResidence;

    @Column(name = "annual_income")
    @JsonProperty("annual_income")
    private String annualIncome;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "doing_business_as")
    @JsonProperty("doing_business_as")
    private String doingBusinessAs;

    @Column(name = "parent_company")
    @JsonProperty("parent_company")
    private String parentCompany;

    @Column(name = "legal_structure")
    @JsonProperty("legal_structure")
    private String legalStructure;

    @JsonFormat(pattern = "MM-dd-yyyy")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfIncorporation;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private CustomerRisk risk;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "customer")
    @ToString.Exclude
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "customer")
    @ToString.Exclude
    private Set<Document> documents = new HashSet<>();

    @JsonProperty("beneficial_owners")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    @ToString.Exclude
    private Set<BeneficialOwner> beneficialOwners = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "customer")
    @ToString.Exclude
    private Set<Account> accounts = new HashSet<>();

    @JsonProperty("markets_served")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    @ToString.Exclude
    private Set<MarketServed> marketsServed = new HashSet<>();

    @JsonProperty("products_offered")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    @ToString.Exclude
    private Set<ProductsOffered> productsOffered = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    @JsonProperty("countries_of_operation")
    @ToString.Exclude
    private Set<CustomerCountry> customerCountries = new HashSet<>();
}
