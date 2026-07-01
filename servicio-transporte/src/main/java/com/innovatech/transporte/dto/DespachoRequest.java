package com.innovatech.transporte.dto;

import lombok.Data;

@Data
public class DespachoRequest {
    private String idPedidoVenta;
    private String clienteId;
    private String sku;
    private String direccionDestino;
    private String distrito;
    private String volumenBulto; // PEQUENO, MEDIANO, GRANDE
}
