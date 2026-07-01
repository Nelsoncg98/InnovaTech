package com.innovatech.clientes;

import com.innovatech.clientes.model.Cliente;
import com.innovatech.clientes.repository.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ClientesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientesApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ClienteRepository repo) {
        return args -> {
            if (repo.findByDocumentoIdentidad("72345678").isEmpty()) {
                Cliente c = new Cliente();
                c.setDocumentoIdentidad("72345678");
                c.setNombreCompleto("Cliente MVP");
                c.setCorreo("mvp@innovatech.com");
                repo.save(c);
            }
        };
    }
}
