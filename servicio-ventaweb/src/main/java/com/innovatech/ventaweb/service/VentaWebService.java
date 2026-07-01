package com.innovatech.ventaweb.service;

import com.innovatech.ventaweb.model.VentaWebRequest;
import com.innovatech.ventaweb.model.VentaWebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaWebService {

    private final WebClient.Builder webClientBuilder;

    public Mono<VentaWebResponse> procesarCheckout(VentaWebRequest request) {
        if (request.getCarrito() == null || request.getCarrito().isEmpty()) {
            return Mono.just(new VentaWebResponse("RECHAZADO", "Carrito vacío."));
        }

        String sku = request.getCarrito().get(0).getSku();
        Integer cantidad = request.getCarrito().get(0).getCantidad();

        // SAGA STEP 1: Reserva de Inventario (Soft-lock)
        Map<String, Object> reservaReq = new HashMap<>();
        reservaReq.put("codigoArticulo", sku);
        reservaReq.put("cantidadReserva", cantidad);
        reservaReq.put("canal", "WEB");
        reservaReq.put("sedeId", "BOD-WEB-CENTRAL");

        return webClientBuilder.build()
                .put()
                .uri("http://servicio-inventario/api/v1/inventario/reserva")
                .bodyValue(reservaReq)
                .retrieve()
                .bodyToMono(Void.class)
                .thenReturn(true)
                .onErrorResume(e -> Mono.error(new RuntimeException("Quiebre de stock en almacén WEB.")))
                
                // SAGA STEP 2: Procesar Pago
                .flatMap(reservaOk -> {
                    Map<String, Object> pagoReq = new HashMap<>();
                    pagoReq.put("monto", 5000.0); // Dummy monto
                    pagoReq.put("tokenTarjeta", request.getDatosPago().getTokenTarjeta());
                    pagoReq.put("moneda", "PEN");
                    
                    return webClientBuilder.build()
                            .post()
                            .uri("http://servicio-pagos/api/v1/pagos/cobrar")
                            .bodyValue(pagoReq)
                            .retrieve()
                            .bodyToMono(Map.class) // Usar map dummy para leer response
                            .onErrorResume(e -> {
                                // SAGA ROLLBACK: Liberar Inventario si falla el pago
                                Map<String, Object> liberarReq = new HashMap<>();
                                liberarReq.put("codigoArticulo", sku);
                                liberarReq.put("cantidadLiberar", cantidad);
                                liberarReq.put("sedeId", "BOD-WEB-CENTRAL");
                                
                                return webClientBuilder.build()
                                        .post()
                                        .uri("http://servicio-inventario/api/v1/inventario/liberar")
                                        .bodyValue(liberarReq)
                                        .retrieve()
                                        .bodyToMono(Void.class)
                                        .then(Mono.error(new RuntimeException("Tarjeta rechazada.")));
                            });
                })
                
                // SAGA STEP 3: Confirmar Inventario (Hard-lock)
                .flatMap(pagoResponse -> {
                    if (!"APROBADO".equals(pagoResponse.get("estado"))) {
                        return Mono.error(new RuntimeException("Tarjeta rechazada por la pasarela."));
                    }
                    
                    Map<String, Object> confirmarReq = new HashMap<>();
                    confirmarReq.put("codigoArticulo", sku);
                    confirmarReq.put("cantidadVendida", cantidad);
                    confirmarReq.put("sedeId", "BOD-WEB-CENTRAL");
                    
                    return webClientBuilder.build()
                            .post()
                            .uri("http://servicio-inventario/api/v1/inventario/confirmar")
                            .bodyValue(confirmarReq)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .then(Mono.defer(() -> {
                                // SAGA STEP 4: Transporte
                                Map<String, Object> transporteReq = new HashMap<>();
                                transporteReq.put("idPedidoVenta", "PED-" + System.currentTimeMillis());
                                transporteReq.put("clienteId", request.getClienteId());
                                transporteReq.put("sku", sku);
                                transporteReq.put("direccionDestino", request.getDireccionDestino() != null ? request.getDireccionDestino() : "Dirección no especificada");
                                
                                return webClientBuilder.build()
                                        .post()
                                        .uri("http://servicio-transporte/api/v1/transporte/despacho")
                                        .bodyValue(transporteReq)
                                        .retrieve()
                                        .bodyToMono(Map.class);
                            }))
                            .flatMap(transporteRes -> {
                                Map<String, Object> notifReq = new HashMap<>();
                                notifReq.put("destinatario", request.getClienteId());
                                notifReq.put("asunto", "Confirmación de Pedido - InnovaTech");
                                notifReq.put("mensaje", "Tu pedido ha sido procesado. Guía Olva Courier: " + transporteRes.get("trackingUrl"));
                                
                                return webClientBuilder.build()
                                        .post()
                                        .uri("http://servicio-notificaciones/api/v1/notificaciones/enviar")
                                        .bodyValue(notifReq)
                                        .retrieve()
                                        .bodyToMono(Void.class)
                                        .thenReturn(transporteRes)
                                        .onErrorResume(e -> {
                                            System.out.println("⚠️ Fallo al notificar pero venta ok: " + e.getMessage());
                                            return Mono.just(transporteRes); // Ignora error de correo para no tumbar la venta
                                        });
                            })
                            .map(transporteRes -> {
                                System.out.println("==================================================");
                                System.out.println("✅ [SERVICIO-NOTIFICACIONES] Email generado para " + request.getClienteId());
                                System.out.println("✅ [SERVICIO-TRANSPORTE] Guía Olva Courier generada: " + transporteRes.get("trackingUrl"));
                                System.out.println("==================================================");
                                return new VentaWebResponse("APROBADO", "Pedido confirmado. Tracking: " + transporteRes.get("trackingUrl"));
                            });
                })
                .onErrorResume(e -> Mono.just(new VentaWebResponse("RECHAZADO", e.getMessage())));
    }
}
