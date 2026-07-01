package com.innovatech.proveedores.dto;

import lombok.Data;

@Data
public class NotaDebitoRequest {
    private String rucProveedor;
    private Double montoReclamo;
}
