package com.innovatech.catalogo.repository;

import com.innovatech.catalogo.model.Articulo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticuloRepository extends MongoRepository<Articulo, String> {

    // Busca por el SKU de negocio (no por el _id de Mongo)
    Optional<Articulo> findByCodigoArticulo(String codigoArticulo);
}
