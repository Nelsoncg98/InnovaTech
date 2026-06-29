package com.innovatech.ventapos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VentaPosApplication {
    public static void main(String[] args) {
        SpringApplication.run(VentaPosApplication.class, args);
    }
}
