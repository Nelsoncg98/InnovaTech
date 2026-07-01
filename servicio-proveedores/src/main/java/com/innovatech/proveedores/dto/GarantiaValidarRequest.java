package com.innovatech.proveedores.dto;

import lombok.Data;

@Data
public class GarantiaValidarRequest {
    private String codigoArticulo;
    private String fechaCompraProveedor;
}
