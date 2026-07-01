package com.innovatech.inventario.config;

import com.innovatech.inventario.model.Inventario;
import com.innovatech.inventario.repository.InventarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final InventarioRepository repository;

    public DataInitializer(InventarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        // Inicializar stock para la tienda física BOD-TIENDA-01 si no existe
        crearInventarioSiNoExiste("SKU-LAP-001", "BOD-TIENDA-01", "POS", 50);
        crearInventarioSiNoExiste("SKU-MON-001", "BOD-TIENDA-01", "POS", 15);
        crearInventarioSiNoExiste("SKU-TEC-001", "BOD-TIENDA-01", "POS", 20);
        crearInventarioSiNoExiste("SKU-AUD-001", "BOD-TIENDA-01", "POS", 10);
        
        // Inicializar stock para el e-commerce BOD-WEB-CENTRAL si no existe
        crearInventarioSiNoExiste("SKU-LAP-001", "BOD-WEB-CENTRAL", "WEB", 100);
        crearInventarioSiNoExiste("SKU-MON-001", "BOD-WEB-CENTRAL", "WEB", 25);
        crearInventarioSiNoExiste("SKU-TEC-001", "BOD-WEB-CENTRAL", "WEB", 40);
        crearInventarioSiNoExiste("SKU-AUD-001", "BOD-WEB-CENTRAL", "WEB", 30);
    }

    private void crearInventarioSiNoExiste(String productoId, String bodegaId, String canal, int stockInicial) {
        if (repository.findByProductoIdAndBodegaLogicaId(productoId, bodegaId).isEmpty()) {
            Inventario nuevo = new Inventario();
            nuevo.setProductoId(productoId);
            nuevo.setBodegaLogicaId(bodegaId);
            nuevo.setCanalAsignado(canal);
            nuevo.setStockFisicoTotal(stockInicial);
            nuevo.setStockDisponibleVenta(stockInicial);
            nuevo.setStockReservado(0);
            nuevo.setStockCuarentena(0);
            nuevo.setUmbralAlerta(10);
            nuevo.setUltimaActualizacion(LocalDateTime.now());
            repository.save(nuevo);
            System.out.println("✅ Inventario inicializado para: " + productoId + " en " + bodegaId);
        }
    }
}
