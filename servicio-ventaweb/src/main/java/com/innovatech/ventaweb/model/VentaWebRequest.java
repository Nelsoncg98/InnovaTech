package com.innovatech.ventaweb.model;

import java.util.List;
import lombok.Data;

@Data
public class VentaWebRequest {
    private String clienteId;
    private String direccionDestino;
    private List<ItemCarrito> carrito;
    private DatosPago datosPago;

    @Data
    public static class ItemCarrito {
        private String sku;
        private Integer cantidad;
    }

    @Data
    public static class DatosPago {
        private String tokenTarjeta;
    }
}
