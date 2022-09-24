package com.migmeninfo.cipservice.batch.document;

import com.migmeninfo.cipservice.common.DocumentType;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class DocumentProcessor implements ItemProcessor<DocumentInput, Document> {
    @Override
    public Document process(DocumentInput documentInput) {
        return Document.builder()
                .url(documentInput.getUrl())
                .customer(Customer.builder().correlationId(documentInput.getCorrelationId()).build())
                .documentType(DocumentType.fromString(documentInput.getDocumentType()))
                .build();
    }


}
