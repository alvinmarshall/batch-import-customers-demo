package com.migmeninfo.cipservice.repository;

import com.migmeninfo.cipservice.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
