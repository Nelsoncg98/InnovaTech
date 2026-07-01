package com.innovatech.clientes.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String documentoIdentidad;
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private String password;
    private String direccion;
    private String distrito;
}
