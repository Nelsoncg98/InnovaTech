package com.innovatech.ventapos.model;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String productoId;
    private Integer cantidad;
    private String documentoCliente;
    private String numeroTarjeta;
}
