package com.innovatech.inventario.service;

import com.innovatech.inventario.model.Inventario;
import com.innovatech.inventario.repository.InventarioRepository;
import com.innovatech.inventario.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventoryStockCanonical consultarStock(String codigoArticulo, String sedeId) {
        Inventario inventario = inventarioRepository.findByProductoIdAndBodegaLogicaId(codigoArticulo, sedeId)
                .orElseThrow(() -> new RuntimeException("Articulo no encontrado en la sede"));
                
        // Validar canal? Podría hacerse aquí si es estricto
        
        InventoryStockCanonical canonico = new InventoryStockCanonical();
        canonico.setCodigoArticulo(inventario.getProductoId());
        canonico.setBodegaLogicaId(inventario.getBodegaLogicaId());
        canonico.setCanalAsignado(inventario.getCanalAsignado());
        canonico.setStockFisicoTotal(inventario.getStockFisicoTotal());
        
        InventoryStockCanonical.EstadoStock estado = new InventoryStockCanonical.EstadoStock();
        estado.setStockReservado(inventario.getStockReservado());
        estado.setStockDisponibleVenta(inventario.getStockDisponibleVenta());
        estado.setStockCuarentena(inventario.getStockCuarentena());
        canonico.setEstadoStock(estado);
        
        canonico.setUmbralAlerta(inventario.getUmbralAlerta());
        canonico.setUltimaActualizacion(inventario.getUltimaActualizacion().toString());
        
        return canonico;
    }

    @Transactional
    public void reservarStock(ReservaRequest request) {
        Inventario inventario = inventarioRepository.findByProductoIdAndBodegaLogicaId(request.getCodigoArticulo(), request.getSedeId())
                .orElseThrow(() -> new RuntimeException("Articulo no encontrado en la sede"));

        if (inventario.getStockDisponibleVenta() >= request.getCantidadReserva()) {
            inventario.setStockDisponibleVenta(inventario.getStockDisponibleVenta() - request.getCantidadReserva());
            inventario.setStockReservado(inventario.getStockReservado() + request.getCantidadReserva());
            inventario.setUltimaActualizacion(LocalDateTime.now());
            inventarioRepository.save(inventario);
        } else {
            throw new RuntimeException("Stock insuficiente para reserva");
        }
    }
    
    @Transactional
    public void confirmarStock(ConfirmarRequest request) {
        Inventario inventario = inventarioRepository.findByProductoIdAndBodegaLogicaId(request.getCodigoArticulo(), request.getSedeId())
                .orElseThrow(() -> new RuntimeException("Articulo no encontrado en la sede"));
                
        if (inventario.getStockReservado() >= request.getCantidadVendida()) {
            inventario.setStockReservado(inventario.getStockReservado() - request.getCantidadVendida());
            inventario.setStockFisicoTotal(inventario.getStockFisicoTotal() - request.getCantidadVendida());
            inventario.setUltimaActualizacion(LocalDateTime.now());
            inventarioRepository.save(inventario);
        } else {
            throw new RuntimeException("Inconsistencia: intentando confirmar más stock del reservado");
        }
    }
    
    @Transactional
    public void liberarStock(LiberarRequest request) {
        Inventario inventario = inventarioRepository.findByProductoIdAndBodegaLogicaId(request.getCodigoArticulo(), request.getSedeId())
                .orElseThrow(() -> new RuntimeException("Articulo no encontrado en la sede"));
                
        if (inventario.getStockReservado() >= request.getCantidadLiberar()) {
            inventario.setStockReservado(inventario.getStockReservado() - request.getCantidadLiberar());
            inventario.setStockDisponibleVenta(inventario.getStockDisponibleVenta() + request.getCantidadLiberar());
            inventario.setUltimaActualizacion(LocalDateTime.now());
            inventarioRepository.save(inventario);
        } else {
            throw new RuntimeException("Inconsistencia: intentando liberar más stock del reservado");
        }
    }
    
    @Transactional
    public void ingresarMercaderia(IngresoRequest request) {
        final String ALMACEN_CENTRAL = "BOD-WEB-CENTRAL";
        
        for (IngresoRequest.Articulo art : request.getArticulos()) {
            
            // Si el destino NO es el almacén central, se asume que es un traspaso hacia una sede (como POS).
            if (!request.getSedeId().equals(ALMACEN_CENTRAL)) {
                Inventario almacen = inventarioRepository.findByProductoIdAndBodegaLogicaId(art.getCodigoArticulo(), ALMACEN_CENTRAL)
                        .orElseThrow(() -> new RuntimeException("Error de Traspaso: El artículo " + art.getCodigoArticulo() + " no existe en el Almacén Central."));
                        
                if (almacen.getStockDisponibleVenta() < art.getCantidad()) {
                    throw new RuntimeException("Error de Traspaso: No hay existencia suficiente de " + art.getCodigoArticulo() + " en el Almacén Central (Actual: " + almacen.getStockDisponibleVenta() + ").");
                }
                
                // 1. Descontar stock del Almacén Central
                almacen.setStockFisicoTotal(almacen.getStockFisicoTotal() - art.getCantidad());
                almacen.setStockDisponibleVenta(almacen.getStockDisponibleVenta() - art.getCantidad());
                almacen.setUltimaActualizacion(LocalDateTime.now());
                inventarioRepository.save(almacen);
            }
            
            // 2. Sumar stock al Destino (Sede)
            Inventario inventarioDestino = inventarioRepository.findByProductoIdAndBodegaLogicaId(art.getCodigoArticulo(), request.getSedeId())
                    .orElseGet(() -> {
                        Inventario nuevo = new Inventario();
                        nuevo.setProductoId(art.getCodigoArticulo());
                        nuevo.setBodegaLogicaId(request.getSedeId());
                        return nuevo;
                    });
                    
            inventarioDestino.setStockFisicoTotal(inventarioDestino.getStockFisicoTotal() + art.getCantidad());
            inventarioDestino.setStockDisponibleVenta(inventarioDestino.getStockDisponibleVenta() + art.getCantidad());
            inventarioDestino.setUltimaActualizacion(LocalDateTime.now());
            inventarioRepository.save(inventarioDestino);
        }
    }
}
