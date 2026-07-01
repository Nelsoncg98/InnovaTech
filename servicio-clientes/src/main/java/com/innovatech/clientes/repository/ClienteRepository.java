package com.innovatech.clientes.repository;

import com.innovatech.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDocumentoIdentidad(String documentoIdentidad);
    Optional<Cliente> findByCorreo(String correo);
}
