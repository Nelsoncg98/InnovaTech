package com.innovatech.ventapos.controller;

import com.innovatech.ventapos.model.CheckoutRequest;
import com.innovatech.ventapos.model.CheckoutResponse;
import com.innovatech.ventapos.service.VentaPosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ventas-pos")
@RequiredArgsConstructor
public class VentaPosController {

    private final VentaPosService ventaPosService;

    // Cumpliendo estrictamente el endpoint definido en el APF3 (Sección 1.2 y 4.2)
    @PostMapping("/checkout")
    public Mono<ResponseEntity<CheckoutResponse>> checkout(@RequestBody CheckoutRequest request) {
        return ventaPosService.procesarCheckout(request)
                .map(ResponseEntity::ok);
    }
}
