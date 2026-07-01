package com.innovatech.ventapos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas_pos")
@Data
public class VentaPos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String documentoCliente;

    @Column(nullable = false)
    private String sedeId;

    @Column(nullable = false)
    private Double montoTotal;

    @Column(nullable = false)
    private String estadoVenta;

    @Column(nullable = false)
    private LocalDateTime fechaVenta;
}
