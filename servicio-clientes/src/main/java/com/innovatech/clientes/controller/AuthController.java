package com.innovatech.clientes.controller;

import com.innovatech.clientes.config.JwtUtil;
import com.innovatech.clientes.dto.AuthRequest;
import com.innovatech.clientes.dto.AuthResponse;
import com.innovatech.clientes.dto.RegisterRequest;
import com.innovatech.clientes.model.Cliente;
import com.innovatech.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clientes/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ClienteRepository clienteRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (clienteRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está registrado");
        }
        if (clienteRepository.findByDocumentoIdentidad(request.getDocumentoIdentidad()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El DNI ya está registrado");
        }

        Cliente cliente = new Cliente();
        cliente.setDocumentoIdentidad(request.getDocumentoIdentidad());
        cliente.setNombreCompleto(request.getNombreCompleto());
        cliente.setCorreo(request.getCorreo());
        cliente.setTelefono(request.getTelefono());
        cliente.setPassword(request.getPassword()); // En PoC guardado en plano, en prod usar BCrypt
        cliente.setDireccion(request.getDireccion());
        cliente.setDistrito(request.getDistrito());

        clienteRepository.save(cliente);

        String token = jwtUtil.generateToken(cliente.getCorreo(), cliente.getDocumentoIdentidad(), cliente.getNombreCompleto());
        return ResponseEntity.ok(new AuthResponse(token, cliente.getDocumentoIdentidad(), cliente.getNombreCompleto(), cliente.getCorreo(), cliente.getDireccion()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(request.getCorreo());

        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo no encontrado");
        }

        Cliente cliente = clienteOpt.get();
        if (!cliente.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(cliente.getCorreo(), cliente.getDocumentoIdentidad(), cliente.getNombreCompleto());
        return ResponseEntity.ok(new AuthResponse(token, cliente.getDocumentoIdentidad(), cliente.getNombreCompleto(), cliente.getCorreo(), cliente.getDireccion()));
    }
}
