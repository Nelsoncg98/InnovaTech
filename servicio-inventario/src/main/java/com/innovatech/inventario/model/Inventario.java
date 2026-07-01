package com.innovatech.inventario.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Data
public class Inventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column()
    private String productoId;
    
    @Column()
    private String bodegaLogicaId = "BOD-WEB-CENTRAL";
    
    @Column()
    private String canalAsignado = "WEB"; // WEB o POS
    
    @Column()
    private Integer stockFisicoTotal = 0;
    
    @Column()
    private Integer stockReservado = 0;
    
    @Column()
    private Integer stockDisponibleVenta = 0;
    
    @Column()
    private Integer stockCuarentena = 0;
    
    @Column()
    private Integer umbralAlerta = 10;
    
    @Column()
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();
}
