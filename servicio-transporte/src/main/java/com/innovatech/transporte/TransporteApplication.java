package com.innovatech.transporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TransporteApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransporteApplication.class, args);
    }
}
