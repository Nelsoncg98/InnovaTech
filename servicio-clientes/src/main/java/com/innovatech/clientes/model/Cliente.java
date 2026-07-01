package com.innovatech.clientes.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "clientes")
@Data
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String documentoIdentidad; // DNI o RUC
    
    @Column(nullable = false, length = 100)
    private String nombreCompleto;
    
    @Column(length = 100)
    private String correo;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 255)
    private String password;
    
    @Column(length = 255)
    private String direccion;
    
    @Column(length = 100)
    private String distrito;
    
    @Column(length = 20)
    private String estado = "ACTIVO"; // ACTIVO, BLOQUEADO
}
