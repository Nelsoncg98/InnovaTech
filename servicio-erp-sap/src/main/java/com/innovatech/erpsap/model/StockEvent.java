package com.innovatech.erpsap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockEvent {
    private String codigoArticulo;
    private Integer cantidad;
    private String operacion; // "INGRESO" o "EGRESO"
    private String referenciaDocumento;
}
