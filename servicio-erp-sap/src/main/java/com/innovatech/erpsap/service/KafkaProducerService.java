package com.innovatech.erpsap.service;

import com.innovatech.erpsap.model.StockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, StockEvent> kafkaTemplate;
    private static final String TOPIC = "stock-events-topic";

    public void publicarIngresoStock(StockEvent evento) {
        kafkaTemplate.send(TOPIC, evento.getCodigoArticulo(), evento);
        System.out.println("Evento enviado a Kafka: " + evento);
    }
}
