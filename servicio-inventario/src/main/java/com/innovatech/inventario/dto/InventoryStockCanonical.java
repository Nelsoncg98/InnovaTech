package com.innovatech.inventario.dto;

import lombok.Data;

@Data
public class InventoryStockCanonical {
    private String codigoArticulo;
    private String bodegaLogicaId;
    private String canalAsignado;
    private Integer stockFisicoTotal;
    private EstadoStock estadoStock;
    private Integer umbralAlerta;
    private String ultimaActualizacion;

    @Data
    public static class EstadoStock {
        private Integer stockReservado;
        private Integer stockDisponibleVenta;
        private Integer stockCuarentena;
    }
}
