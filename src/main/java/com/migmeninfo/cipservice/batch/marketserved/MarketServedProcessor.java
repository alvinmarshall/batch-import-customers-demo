package com.migmeninfo.cipservice.batch.marketserved;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.MarketServed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MarketServedProcessor implements ItemProcessor<MarketServedInput, MarketServed> {
    @Override
    public MarketServed process(MarketServedInput marketServedInput) {
        MarketServed marketServed = MarketServed.builder()
                .market(marketServedInput.getMarket())
                .customer(Customer.builder().correlationId(marketServedInput.getCorrelationId()).build())
                .build();
        return marketServed;
    }


}
