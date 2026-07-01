package com.innovatech.catalogo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Entidad MongoDB del catálogo de productos.
 * Contiene precios y atributos del producto (NO stock — eso es inventario).
 */
@Data
@Document(collection = "articulos")
public class Articulo {

    @Id
    private String id;

    private String codigoArticulo;   // SKU: ej. "SKU-LAP-001"
    private String categoria;        // ej. "COMPUTO", "AUDIO"

    private Detalles detalles;
    private Precios precios;
    private List<String> imagenesUrl;

    @Data
    public static class Detalles {
        private String marca;
        private String descripcion;
        private Map<String, String> especificacionesTecnicas;
    }

    @Data
    public static class Precios {
        private double precioBase;
        private String moneda;
    }
}
