package com.migmeninfo.cipservice.repository;

import com.migmeninfo.cipservice.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
