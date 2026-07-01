package com.innovatech.inventario.dto;

import lombok.Data;

@Data
public class LiberarRequest {
    private String codigoArticulo;
    private Integer cantidadLiberar;
    private String sedeId;
}
