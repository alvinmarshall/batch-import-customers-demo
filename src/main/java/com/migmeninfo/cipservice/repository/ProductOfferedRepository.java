package com.migmeninfo.cipservice.repository;

import com.migmeninfo.cipservice.domain.entity.ProductsOffered;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOfferedRepository extends JpaRepository<ProductsOffered, String> {
}
