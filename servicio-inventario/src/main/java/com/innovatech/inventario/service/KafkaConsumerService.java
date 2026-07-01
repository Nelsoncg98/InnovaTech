package com.innovatech.inventario.service;

import com.innovatech.inventario.model.Inventario;
import com.innovatech.inventario.model.StockEvent;
import com.innovatech.inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final InventarioRepository inventarioRepository;

    @KafkaListener(topics = "stock-events-topic", groupId = "inventario-group")
    @Transactional
    public void consumirEventoStock(StockEvent evento) {
        System.out.println("Evento recibido desde Kafka (ERP): " + evento);

        Inventario stock = inventarioRepository.findByProductoIdAndBodegaLogicaId(evento.getCodigoArticulo(), "BOD-WEB-CENTRAL")
            .orElseGet(() -> {
                // Si el producto es nuevo en el inventario, lo creamos (Bodega Web por defecto en el ERP central)
                Inventario nuevo = new Inventario();
                nuevo.setProductoId(evento.getCodigoArticulo());
                nuevo.setBodegaLogicaId("BOD-WEB-CENTRAL");
                nuevo.setCanalAsignado("WEB");
                nuevo.setStockFisicoTotal(0);
                nuevo.setStockReservado(0);
                return nuevo;
            });

        if ("INGRESO".equals(evento.getOperacion())) {
            stock.setStockFisicoTotal(stock.getStockFisicoTotal() + evento.getCantidad());
            System.out.println("Stock sumado exitosamente al Kardex. Nuevo stock físico: " + stock.getStockFisicoTotal());
        } else if ("EGRESO".equals(evento.getOperacion())) {
            stock.setStockFisicoTotal(stock.getStockFisicoTotal() - evento.getCantidad());
            System.out.println("Stock restado exitosamente (Rollback / Devolución).");
        }

        inventarioRepository.save(stock);
    }
}
