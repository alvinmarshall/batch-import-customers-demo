package com.migmeninfo.cipservice.batch.productoffered;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.ProductsOffered;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ProductOfferedProcessor implements ItemProcessor<ProductOfferedInput, ProductsOffered> {
    @Override
    public ProductsOffered process(ProductOfferedInput productOfferedInput) {
        ProductsOffered productsOffered = ProductsOffered.builder()
                .productCode(productOfferedInput.getProduct())
                .customer(Customer.builder().correlationId(productOfferedInput.getCorrelationId()).build())
                .build();
        return productsOffered;
    }


}
