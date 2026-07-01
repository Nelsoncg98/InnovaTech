package com.innovatech.inventario.repository;

import com.innovatech.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoIdAndBodegaLogicaId(String productoId, String bodegaLogicaId);
}
