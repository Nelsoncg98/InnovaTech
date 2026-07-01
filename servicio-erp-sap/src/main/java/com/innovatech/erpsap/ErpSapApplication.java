package com.innovatech.erpsap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ErpSapApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErpSapApplication.class, args);
    }
}
