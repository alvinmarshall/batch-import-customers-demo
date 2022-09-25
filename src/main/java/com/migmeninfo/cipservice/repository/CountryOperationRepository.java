package com.migmeninfo.cipservice.repository;

import com.migmeninfo.cipservice.domain.entity.CustomerCountry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryOperationRepository extends JpaRepository<CustomerCountry, String> {
}
