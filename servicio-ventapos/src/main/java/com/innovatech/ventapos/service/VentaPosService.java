package com.innovatech.ventapos.service;

import com.innovatech.ventapos.model.CheckoutRequest;
import com.innovatech.ventapos.model.CheckoutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VentaPosService {

    private final WebClient.Builder webClientBuilder;

    public Mono<CheckoutResponse> procesarCheckout(CheckoutRequest request) {
        return webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("servicio-inventario")
                        .path("/api/inventario/" + request.getProductoId() + "/descontar")
                        .queryParam("cantidad", request.getCantidad())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> new CheckoutResponse("APROBADO", "Checkout Orquestado Exitosamente. " + response))
                .onErrorResume(e -> Mono.just(new CheckoutResponse("RECHAZADO", "Fallo en el Checkout. Error de Stock o Comunicación.")));
    }
}
