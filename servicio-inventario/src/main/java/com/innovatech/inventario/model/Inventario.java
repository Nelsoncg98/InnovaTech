package com.innovatech.inventario.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventario")
@Data
public class Inventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String productoId;
    
    @Column(nullable = false)
    private Integer stockDisponible;
}
