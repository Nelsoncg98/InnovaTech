package com.innovatech.inventario.dto;

import lombok.Data;

@Data
public class ReservaRequest {
    private String codigoArticulo;
    private Integer cantidadReserva;
    private String canal;
    private String sedeId; // WEB, POS
}
