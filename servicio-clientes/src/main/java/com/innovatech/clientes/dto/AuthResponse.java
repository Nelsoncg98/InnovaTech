package com.innovatech.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String dni;
    private String nombre;
    private String correo;
    private String direccion;
}
