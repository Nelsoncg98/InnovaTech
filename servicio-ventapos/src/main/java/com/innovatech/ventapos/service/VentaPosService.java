package com.innovatech.ventapos.service;

import com.innovatech.ventapos.model.SalesOrderCanonical;
import com.innovatech.ventapos.model.CheckoutResponse;
import com.innovatech.ventapos.model.PagoRequest;
import com.innovatech.ventapos.model.PagoResponse;
import com.innovatech.ventapos.model.VentaPos;
import com.innovatech.ventapos.repository.VentaPosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaPosService {

    private final WebClient.Builder webClientBuilder;
    private final VentaPosRepository ventaPosRepository;

    public Mono<CheckoutResponse> procesarCheckout(SalesOrderCanonical request) {
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            return Mono.just(new CheckoutResponse("RECHAZADO", "No hay productos en la orden."));
        }
        
        String productoId = request.getDetalles().get(0).getCodigoArticulo();
        Integer cantidad = request.getDetalles().get(0).getCantidad();
        String documentoCliente = request.getCliente().getNumeroDocumento();

        // SAGA STEP 1: Validar Cliente
        return webClientBuilder.build()
                .get()
                .uri("http://servicio-clientes/api/v1/clientes/" + documentoCliente)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Cliente no existe o bloqueado.")))
                
                // SAGA STEP 2: Reserva de Inventario (Soft-lock)
                .flatMap(clienteResponse -> {
                    Map<String, Object> reservaReq = new HashMap<>();
                    reservaReq.put("codigoArticulo", productoId);
                    reservaReq.put("cantidadReserva", cantidad);
                    reservaReq.put("sedeId", request.getSedeId());
                    
                    return webClientBuilder.build()
                            .put()
                            .uri("http://servicio-inventario/api/v1/inventario/reserva")
                            .bodyValue(reservaReq)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .thenReturn(true)
                            .onErrorResume(e -> Mono.error(new RuntimeException("Sin stock suficiente en POS.")));
                })
                
                // SAGA STEP 3: Procesar Pago
                .flatMap(reservaOk -> {
                    PagoRequest pagoReq = new PagoRequest(
                            request.getPago().getMontoTotal(),
                            request.getPago().getTokenTarjeta(),
                            "PEN"
                    );
                    return webClientBuilder.build()
                            .post()
                            .uri("http://servicio-pagos/api/v1/pagos/cobrar")
                            .bodyValue(pagoReq)
                            .retrieve()
                            .bodyToMono(PagoResponse.class)
                            .onErrorResume(e -> {
                                // SAGA ROLLBACK: Liberar Inventario si falla el pago (HTTP 4xx/5xx)
                                Map<String, Object> liberarReq = new HashMap<>();
                                liberarReq.put("codigoArticulo", productoId);
                                liberarReq.put("cantidadLiberar", cantidad);
                                liberarReq.put("sedeId", request.getSedeId());
                                
                                return webClientBuilder.build()
                                        .post()
                                        .uri("http://servicio-inventario/api/v1/inventario/liberar")
                                        .bodyValue(liberarReq)
                                        .retrieve()
                                        .bodyToMono(Void.class)
                                        .then(Mono.error(new RuntimeException("Pago rechazado. Rollback de inventario ejecutado.")));
                            });
                })
                
                // SAGA STEP 4: Confirmar Inventario (Hard-lock)
                .flatMap(pagoResponse -> {
                    if (!"APROBADO".equals(pagoResponse.getEstado())) {
                        return Mono.error(new RuntimeException("Tarjeta rechazada por la pasarela."));
                    }
                    
                    Map<String, Object> confirmarReq = new HashMap<>();
                    confirmarReq.put("codigoArticulo", productoId);
                    confirmarReq.put("cantidadVendida", cantidad);
                    confirmarReq.put("sedeId", request.getSedeId());
                    
                    return webClientBuilder.build()
                            .post()
                            .uri("http://servicio-inventario/api/v1/inventario/confirmar")
                            .bodyValue(confirmarReq)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .then(Mono.defer(() -> {
                                // SAGA STEP 5: Guardar en BD (DBVen)
                                VentaPos venta = new VentaPos();
                                venta.setDocumentoCliente(request.getCliente().getNumeroDocumento());
                                venta.setSedeId(request.getSedeId());
                                venta.setMontoTotal(request.getPago().getMontoTotal());
                                venta.setEstadoVenta("APROBADO");
                                venta.setFechaVenta(LocalDateTime.now());
                                
                                // Bloqueante pero necesario para simplificar el flujo WebFlux en este PoC
                                ventaPosRepository.save(venta);
                                System.out.println("✅ SAGA Súper Exitosa: Venta registrada en DBVen (ID: " + venta.getId() + ")");
                                
                                return Mono.just(new CheckoutResponse("APROBADO", "Venta procesada con éxito y registrada en base de datos."));
                            }));
                })
                .onErrorResume(e -> Mono.just(new CheckoutResponse("RECHAZADO", "SAGA Fallida: " + e.getMessage())));
    }
}
