package com.innovatech.ventapos.repository;

import com.innovatech.ventapos.model.VentaPos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaPosRepository extends JpaRepository<VentaPos, Long> {
}
