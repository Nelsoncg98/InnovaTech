package com.innovatech.ventaweb.controller;

import com.innovatech.ventaweb.model.VentaWebRequest;
import com.innovatech.ventaweb.model.VentaWebResponse;
import com.innovatech.ventaweb.service.VentaWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ventas-web")
@RequiredArgsConstructor
public class VentaWebController {

    private final VentaWebService ventaWebService;

    @PostMapping("/checkout")
    public Mono<ResponseEntity<VentaWebResponse>> checkout(@RequestBody VentaWebRequest request) {
        return ventaWebService.procesarCheckout(request)
                .map(ResponseEntity::ok);
    }
}
