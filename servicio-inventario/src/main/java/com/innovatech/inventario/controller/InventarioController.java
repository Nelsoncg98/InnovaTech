package com.innovatech.inventario.controller;

import com.innovatech.inventario.service.InventarioService;
import com.innovatech.inventario.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping("/{codigoArticulo}")
    public ResponseEntity<InventoryStockCanonical> consultarStock(
            @PathVariable String codigoArticulo,
            @RequestParam String sedeId) {
        return ResponseEntity.ok(inventarioService.consultarStock(codigoArticulo, sedeId));
    }

    @PutMapping("/reserva")
    public ResponseEntity<Void> reserva(@RequestBody ReservaRequest request) {
        inventarioService.reservarStock(request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/confirmar")
    public ResponseEntity<Void> confirmar(@RequestBody ConfirmarRequest request) {
        inventarioService.confirmarStock(request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/liberar")
    public ResponseEntity<Void> liberar(@RequestBody LiberarRequest request) {
        inventarioService.liberarStock(request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/ingreso")
    public ResponseEntity<?> ingreso(@RequestBody IngresoRequest request) {
        try {
            inventarioService.ingresarMercaderia(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
