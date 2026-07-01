package com.innovatech.pagos.controller;

import com.innovatech.pagos.model.PagoRequest;
import com.innovatech.pagos.model.PagoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    @PostMapping("/cobrar")
    public ResponseEntity<PagoResponse> procesarPago(@RequestBody PagoRequest request) {
        // Simulador de Pasarela de Pagos (Mock Niubiz)
        // Por simplicidad, si el token no es "0000", lo aprobamos.
        if (request.getTokenTarjeta() != null && request.getTokenTarjeta().startsWith("0000")) {
            return ResponseEntity.badRequest().body(new PagoResponse("RECHAZADO", null));
        }
        
        return ResponseEntity.ok(new PagoResponse("APROBADO", UUID.randomUUID().toString()));
    }
}
