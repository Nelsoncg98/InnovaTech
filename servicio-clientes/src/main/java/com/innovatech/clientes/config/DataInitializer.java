package com.innovatech.clientes.config;

import com.innovatech.clientes.model.Cliente;
import com.innovatech.clientes.repository.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ClienteRepository repository;

    public DataInitializer(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        // Inicialización segura: solo inserta los clientes de prueba si no existen en BD
        crearClienteSiNoExiste("12345678", "PEDRO PEREZ", "pedro@test.com", "911222333");
        crearClienteSiNoExiste("87654321", "ANA GOMEZ", "ana@test.com", "988777666");
    }

    private void crearClienteSiNoExiste(String documento, String nombre, String correo, String telefono) {
        if (repository.findByDocumentoIdentidad(documento).isEmpty()) {
            Cliente nuevo = new Cliente();
            nuevo.setDocumentoIdentidad(documento);
            nuevo.setNombreCompleto(nombre);
            nuevo.setCorreo(correo);
            nuevo.setTelefono(telefono);
            nuevo.setEstado("ACTIVO");
            repository.save(nuevo);
            System.out.println("✅ Cliente inicializado: " + nombre);
        }
    }
}
