package com.innovatech.proveedores.controller;

import com.innovatech.proveedores.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/proveedores")
public class ProveedoresController {

    @GetMapping("/{ruc}")
    public ResponseEntity<Map<String, Object>> consultarProveedor(@PathVariable String ruc) {
        Map<String, Object> response = new HashMap<>();
        response.put("ruc", ruc);
        response.put("razonSocial", "PROVEEDOR TECNOLOGICO S.A.C.");
        response.put("estado", "ACTIVO");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/guia/validar")
    public ResponseEntity<Void> validarGuia(@RequestBody GuiaValidarRequest request) {
        if (request.getGuiaRemision() != null && request.getGuiaRemision().startsWith("FALSO")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Guia fraudulenta
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/garantia/validar")
    public ResponseEntity<Void> validarGarantia(@RequestBody GarantiaValidarRequest request) {
        if (request.getFechaCompraProveedor() != null && request.getFechaCompraProveedor().startsWith("2020")) {
            return ResponseEntity.badRequest().build(); // 400 Garantía expirada
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/nota-debito")
    public ResponseEntity<Void> emitirNotaDebito(@RequestBody NotaDebitoRequest request) {
        // Simulación: Envío de evento a Kafka para asentar contablemente en SAP
        System.out.println("Enviando evento a Kafka [topico: sap-notas-debito] -> RUC: " + request.getRucProveedor() + " | Monto: " + request.getMontoReclamo());
        return ResponseEntity.accepted().build(); // 202 Accepted
    }
}
