package com.migmeninfo.cipservice.repository;

import com.migmeninfo.cipservice.domain.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, String> {
}
