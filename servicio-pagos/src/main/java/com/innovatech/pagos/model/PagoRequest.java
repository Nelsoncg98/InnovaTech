package com.innovatech.pagos.model;

import lombok.Data;

@Data
public class PagoRequest {
    private Double monto;
    private String tokenTarjeta;
    private String moneda = "PEN";
}
