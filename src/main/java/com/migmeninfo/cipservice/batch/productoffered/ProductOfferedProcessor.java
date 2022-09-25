package com.migmeninfo.cipservice.batch.productoffered;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.ProductsOffered;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ProductOfferedProcessor implements ItemProcessor<ProductOfferedInput, ProductsOffered> {
    private final CustomerRepository customerRepository;

    public ProductOfferedProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public ProductsOffered process(ProductOfferedInput productOfferedInput) {
        Optional<Customer> optionalCustomer = customerRepository.findByCorrelationId(productOfferedInput.getCorrelationId());
        if (optionalCustomer.isEmpty()) return null;
        Customer customer = optionalCustomer.get();
        return ProductsOffered.builder()
                .productCode(productOfferedInput.getProduct())
                .customer(customer)
                .build();
    }


}
