package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Entrenador;
import com.infinityfutbol.entity.enums.EstadoEntrenador;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntrenadorRepository
        extends JpaRepository<Entrenador, String> {

    @EntityGraph(attributePaths = "usuario")
    List<Entrenador> findByEstadoOrderByApellidosAscNombresAsc(
            EstadoEntrenador estado
    );

    @EntityGraph(attributePaths = "usuario")
    Optional<Entrenador> findByIdEntrenadorAndEstado(
            String idEntrenador,
            EstadoEntrenador estado
    );
}