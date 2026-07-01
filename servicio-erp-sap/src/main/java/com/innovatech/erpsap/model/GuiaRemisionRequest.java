package com.innovatech.erpsap.model;

import lombok.Data;
import java.util.List;

@Data
public class GuiaRemisionRequest {
    private String guiaRemisionId;
    private String proveedorId;
    private List<ArticuloIngreso> articulos;

    @Data
    public static class ArticuloIngreso {
        private String codigoArticulo;
        private Integer cantidadRecibida;
    }
}
