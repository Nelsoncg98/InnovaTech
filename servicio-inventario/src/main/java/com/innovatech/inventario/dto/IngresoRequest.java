package com.innovatech.inventario.dto;

import java.util.List;
import lombok.Data;

@Data
public class IngresoRequest {
    private String origen; // EJ: "PROVEEDOR-01"
    private String sedeId;
    private List<Articulo> articulos;

    @Data
    public static class Articulo {
        private String codigoArticulo;
        private Integer cantidad;
    }
}
