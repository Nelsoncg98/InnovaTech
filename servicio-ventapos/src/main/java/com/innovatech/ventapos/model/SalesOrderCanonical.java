package com.innovatech.ventapos.model;

import java.util.List;
import lombok.Data;

@Data
public class SalesOrderCanonical {
    private String ordenId;
    private String canalOrigen;
    private String sedeId;
    private String cajaId;
    private String fechaTransaccion;
    private Cliente cliente;
    private List<Detalle> detalles;
    private Pago pago;

    @Data
    public static class Cliente {
        private String numeroDocumento;
        private String nombreCompleto;
    }

    @Data
    public static class Detalle {
        private String codigoArticulo;
        private String descripcion;
        private Integer cantidad;
        private Double precioUnitario;
    }

    @Data
    public static class Pago {
        private String metodo;
        private String tokenTarjeta;
        private Double montoTotal;
        private String estado;
    }
}
