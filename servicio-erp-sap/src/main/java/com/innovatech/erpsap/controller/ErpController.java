package com.innovatech.erpsap.controller;

import com.innovatech.erpsap.model.GuiaRemisionRequest;
import com.innovatech.erpsap.model.StockEvent;
import com.innovatech.erpsap.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/erp")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ErpController {

    private final KafkaProducerService kafkaProducer;

    @PostMapping("/recepcion-mercaderia")
    public ResponseEntity<Map<String, String>> recibirMercaderia(@RequestBody GuiaRemisionRequest guia) {
        // Por cada artículo en la Guía, publicamos un evento asíncrono hacia Kafka
        for (GuiaRemisionRequest.ArticuloIngreso articulo : guia.getArticulos()) {
            StockEvent evento = new StockEvent(
                articulo.getCodigoArticulo(),
                articulo.getCantidadRecibida(),
                "INGRESO",
                guia.getGuiaRemisionId()
            );
            kafkaProducer.publicarIngresoStock(evento);
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("estado", "EXITO");
        response.put("mensaje", "Guía procesada en SAP. Eventos publicados en Kafka exitosamente.");
        response.put("guiaRemisionId", guia.getGuiaRemisionId());
        
        return ResponseEntity.ok(response);
    }
}
