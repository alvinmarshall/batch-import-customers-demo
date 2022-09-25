package com.migmeninfo.cipservice.controller;

import com.migmeninfo.cipservice.domain.service.CustomerBatchService;
import com.migmeninfo.cipservice.domain.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customers")
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerBatchService customerBatchService;

    public CustomerController(CustomerService customerService, CustomerBatchService customerBatchService) {
        this.customerService = customerService;
        this.customerBatchService = customerBatchService;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCustomer(@PathVariable("id") String customerId) {
        return ResponseEntity.ok(customerService.findById(customerId));
    }

    @GetMapping("batch")
    public ResponseEntity<?> processBatch() {
        customerBatchService.processBatch();
        return ResponseEntity.ok("started");
    }
}
