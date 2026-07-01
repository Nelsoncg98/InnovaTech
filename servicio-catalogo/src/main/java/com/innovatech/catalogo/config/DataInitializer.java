package com.innovatech.catalogo.config;

import com.innovatech.catalogo.model.Articulo;
import com.innovatech.catalogo.repository.ArticuloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Inicializa la colección MongoDB con productos de prueba al arrancar.
 * Solo inserta si la colección está vacía.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ArticuloRepository repository;

    public DataInitializer(ArticuloRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return; // Evita duplicar al reiniciar

        // ── SKU-LAP-001: Laptop Gamer ──────────────────────────────────
        Articulo lap001 = new Articulo();
        lap001.setCodigoArticulo("SKU-LAP-001");
        lap001.setCategoria("COMPUTO");

        Articulo.Detalles dLap = new Articulo.Detalles();
        dLap.setMarca("Asus");
        dLap.setDescripcion("Laptop Gamer 15.6 pulgadas");
        dLap.setEspecificacionesTecnicas(Map.of("ram", "16GB", "procesador", "Intel Core i7"));
        lap001.setDetalles(dLap);

        Articulo.Precios pLap = new Articulo.Precios();
        pLap.setPrecioBase(3500.00);
        pLap.setMoneda("PEN");
        lap001.setPrecios(pLap);
        lap001.setImagenesUrl(List.of("https://cdn.innovatech.com/img/lap001.jpg"));

        // ── SKU-MON-001: Monitor ──────────────────────────────────────
        Articulo mon001 = new Articulo();
        mon001.setCodigoArticulo("SKU-MON-001");
        mon001.setCategoria("COMPUTO");

        Articulo.Detalles dMon = new Articulo.Detalles();
        dMon.setMarca("LG");
        dMon.setDescripcion("Monitor 24 pulgadas Full HD");
        dMon.setEspecificacionesTecnicas(Map.of("resolucion", "1920x1080", "panel", "IPS"));
        mon001.setDetalles(dMon);

        Articulo.Precios pMon = new Articulo.Precios();
        pMon.setPrecioBase(850.00);
        pMon.setMoneda("PEN");
        mon001.setPrecios(pMon);
        mon001.setImagenesUrl(List.of("https://cdn.innovatech.com/img/mon001.jpg"));

        // ── SKU-TEC-001: Teclado ──────────────────────────────────────
        Articulo tec001 = new Articulo();
        tec001.setCodigoArticulo("SKU-TEC-001");
        tec001.setCategoria("PERIFERICOS");

        Articulo.Detalles dTec = new Articulo.Detalles();
        dTec.setMarca("Redragon");
        dTec.setDescripcion("Teclado Mecanico Gaming RGB");
        dTec.setEspecificacionesTecnicas(Map.of("switch", "Red", "iluminacion", "RGB"));
        tec001.setDetalles(dTec);

        Articulo.Precios pTec = new Articulo.Precios();
        pTec.setPrecioBase(250.00);
        pTec.setMoneda("PEN");
        tec001.setPrecios(pTec);
        tec001.setImagenesUrl(List.of("https://cdn.innovatech.com/img/tec001.jpg"));

        // ── SKU-AUD-001: Audífonos ────────────────────────────────────
        Articulo aud001 = new Articulo();
        aud001.setCodigoArticulo("SKU-AUD-001");
        aud001.setCategoria("AUDIO");

        Articulo.Detalles dAud = new Articulo.Detalles();
        dAud.setMarca("HyperX");
        dAud.setDescripcion("Audifonos Gamer 7.1 Surround");
        dAud.setEspecificacionesTecnicas(Map.of("conectividad", "USB", "sonido", "7.1 Virtual"));
        aud001.setDetalles(dAud);

        Articulo.Precios pAud = new Articulo.Precios();
        pAud.setPrecioBase(320.00);
        pAud.setMoneda("PEN");
        aud001.setPrecios(pAud);
        aud001.setImagenesUrl(List.of("https://cdn.innovatech.com/img/aud001.jpg"));

        repository.saveAll(List.of(lap001, mon001, tec001, aud001));
        System.out.println("✅ Catálogo inicializado con 4 productos de prueba.");
    }
}
