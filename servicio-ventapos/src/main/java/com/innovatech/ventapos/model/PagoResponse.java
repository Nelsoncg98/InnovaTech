package com.innovatech.ventapos.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PagoResponse {
    private String estado;
    private String idTransaccion;
}
