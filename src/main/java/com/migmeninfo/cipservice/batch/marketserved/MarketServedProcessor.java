package com.migmeninfo.cipservice.batch.marketserved;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.MarketServed;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class MarketServedProcessor implements ItemProcessor<MarketServedInput, MarketServed> {
    private final CustomerRepository customerRepository;

    public MarketServedProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public MarketServed process(MarketServedInput marketServedInput) {
        Optional<Customer> customerOptional = customerRepository
                .findByCorrelationId(marketServedInput.getCorrelationId());

        if (customerOptional.isEmpty()) return null;
        Customer customer = customerOptional.get();
        return MarketServed.builder()
                .market(marketServedInput.getMarket())
                .customer(customer)
                .build();
    }


}
