package com.migmeninfo.cipservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CipServiceApplication.class, args);
    }

}
