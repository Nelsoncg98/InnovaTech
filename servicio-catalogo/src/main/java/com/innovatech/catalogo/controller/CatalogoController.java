package com.innovatech.catalogo.controller;

import com.innovatech.catalogo.model.Articulo;
import com.innovatech.catalogo.repository.ArticuloRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrato APF2: SVC-CAT-01
 * GET /api/v1/catalogo/productos          → Listado paginado
 * GET /api/v1/catalogo/productos/{sku}    → Detalle con precios
 */
@RestController
@RequestMapping("/api/v1/catalogo/productos")
public class CatalogoController {

    private final ArticuloRepository repository;

    public CatalogoController(ArticuloRepository repository) {
        this.repository = repository;
    }

    // GET /api/v1/catalogo/productos
    @GetMapping
    public ResponseEntity<List<Articulo>> listarProductos() {
        return ResponseEntity.ok(repository.findAll());
    }

    // GET /api/v1/catalogo/productos/{codigoArticulo}
    @GetMapping("/{codigoArticulo}")
    public ResponseEntity<Articulo> obtenerProducto(@PathVariable String codigoArticulo) {
        return repository.findByCodigoArticulo(codigoArticulo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
