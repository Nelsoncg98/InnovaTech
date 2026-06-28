package com.innovatech.inventario.service;

import com.innovatech.inventario.model.Inventario;
import com.innovatech.inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public Integer getStock(String productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .map(Inventario::getStockDisponible)
                .orElse(0);
    }

    @Transactional
    public boolean descontarStock(String productoId, Integer cantidad) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en inventario"));

        if (inventario.getStockDisponible() >= cantidad) {
            inventario.setStockDisponible(inventario.getStockDisponible() - cantidad);
            inventarioRepository.save(inventario);
            return true;
        }
        return false;
    }
}
