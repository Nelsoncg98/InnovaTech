package com.innovatech.inventario.controller;

import com.innovatech.inventario.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping("/{productoId}/stock")
    public ResponseEntity<Integer> consultarStock(@PathVariable String productoId) {
        return ResponseEntity.ok(inventarioService.getStock(productoId));
    }

    @PostMapping("/{productoId}/descontar")
    public ResponseEntity<String> descontarStock(
            @PathVariable String productoId, 
            @RequestParam Integer cantidad) {
        
        boolean exito = inventarioService.descontarStock(productoId, cantidad);
        if (exito) {
            return ResponseEntity.ok("Stock descontado exitosamente");
        } else {
            return ResponseEntity.badRequest().body("Stock insuficiente o producto no encontrado");
        }
    }
}
