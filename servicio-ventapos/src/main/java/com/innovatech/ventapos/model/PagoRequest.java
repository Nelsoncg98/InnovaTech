package com.innovatech.ventapos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoRequest {
    private Double monto;
    private String tokenTarjeta;
    private String moneda;
}
