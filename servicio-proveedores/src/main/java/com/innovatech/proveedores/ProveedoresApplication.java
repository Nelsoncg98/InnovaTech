package com.innovatech.proveedores;

import com.innovatech.proveedores.model.Inventario;
import com.innovatech.proveedores.repository.InventarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ProveedoresApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProveedoresApplication.class, args);
    }

    @Bean
    public CommandLineRunner initInventario(InventarioRepository repo) {
        return args -> {
            if (repo.findByProductoId("SKU-123").isEmpty()) {
                Inventario inv = new Inventario();
                inv.setProductoId("SKU-123");
                inv.setStockDisponible(100);
                repo.save(inv);
            }
        };
    }
}
