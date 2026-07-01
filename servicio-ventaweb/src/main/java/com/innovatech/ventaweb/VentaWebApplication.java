package com.innovatech.ventaweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VentaWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(VentaWebApplication.class, args);
    }
}
