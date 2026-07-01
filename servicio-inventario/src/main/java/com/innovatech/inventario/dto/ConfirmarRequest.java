package com.innovatech.inventario.dto;

import lombok.Data;

@Data
public class ConfirmarRequest {
    private String codigoArticulo;
    private Integer cantidadVendida;
    private String sedeId;
}
