package com.migmeninfo.cipservice.batch.customer.document;

import com.migmeninfo.cipservice.common.DocumentType;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.Document;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class DocumentProcessor implements ItemProcessor<DocumentInput, Document> {
    private final CustomerRepository customerRepository;

    public DocumentProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Document process(DocumentInput documentInput) {
        Optional<Customer> optionalCustomer = customerRepository.findByCorrelationId(documentInput.getCorrelationId());
        if (optionalCustomer.isEmpty()) return null;
        Customer customer = optionalCustomer.get();
        return Document.builder()
                .url(documentInput.getUrl())
                .customer(customer)
                .documentType(DocumentType.fromString(documentInput.getDocumentType()))
                .build();
    }


}
