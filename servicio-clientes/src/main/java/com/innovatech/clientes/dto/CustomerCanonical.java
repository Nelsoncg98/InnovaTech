package com.innovatech.clientes.dto;

import lombok.Data;

@Data
public class CustomerCanonical {
    private String clienteId;
    private String numeroDocumento;
    private String nombreCompleto;
    private Contacto contacto;
    private String estadoPerfil;

    @Data
    public static class Contacto {
        private String email;
        private String telefonoMovil;
    }
}
