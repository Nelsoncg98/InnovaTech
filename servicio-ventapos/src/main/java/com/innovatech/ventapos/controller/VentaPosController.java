package com.innovatech.ventapos.controller;

import com.innovatech.ventapos.model.SalesOrderCanonical;
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

    @PostMapping("/checkout")
    public Mono<ResponseEntity<CheckoutResponse>> checkout(@RequestBody SalesOrderCanonical request) {
        return ventaPosService.procesarCheckout(request)
                .map(ResponseEntity::ok);
    }
}
