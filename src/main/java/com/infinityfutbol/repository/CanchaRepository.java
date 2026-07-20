package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Cancha;
import com.infinityfutbol.entity.enums.EstadoCancha;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CanchaRepository
        extends JpaRepository<Cancha, String> {

    @EntityGraph(attributePaths = {
            "sede",
            "sede.distrito"
    })
    List<Cancha> findByEstadoAndSede_EstadoTrueOrderBySede_NombreAscNumeroCanchaAsc(
            EstadoCancha estado
    );

    @EntityGraph(attributePaths = {
            "sede",
            "sede.distrito"
    })
    Optional<Cancha> findByIdCanchaAndEstado(
            String idCancha,
            EstadoCancha estado
    );
}