package com.innovatech.clientes.controller;

import com.innovatech.clientes.model.Cliente;
import com.innovatech.clientes.dto.CustomerCanonical;
import com.innovatech.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;

    @GetMapping("/{numeroDocumento}")
    public ResponseEntity<CustomerCanonical> validarCliente(@PathVariable String numeroDocumento) {
        return clienteRepository.findByDocumentoIdentidad(numeroDocumento)
                .map(cliente -> {
                    CustomerCanonical canonico = new CustomerCanonical();
                    canonico.setClienteId("CLI-" + cliente.getId());
                    canonico.setNumeroDocumento(cliente.getDocumentoIdentidad());
                    canonico.setNombreCompleto(cliente.getNombreCompleto());
                    
                    CustomerCanonical.Contacto contacto = new CustomerCanonical.Contacto();
                    contacto.setEmail(cliente.getCorreo());
                    contacto.setTelefonoMovil(cliente.getTelefono());
                    canonico.setContacto(contacto);
                    
                    canonico.setEstadoPerfil(cliente.getEstado());
                    return ResponseEntity.ok(canonico);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
