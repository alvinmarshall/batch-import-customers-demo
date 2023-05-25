package com.migmeninfo.cipservice.repository;

import com.migmeninfo.cipservice.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, String> {
}
