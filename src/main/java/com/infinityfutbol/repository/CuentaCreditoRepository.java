package com.infinityfutbol.repository;

import com.infinityfutbol.entity.CuentaCredito;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaCreditoRepository
        extends JpaRepository<CuentaCredito, String> {

    boolean existsByAlumno_IdAlumno(
            String idAlumno
    );

    @EntityGraph(attributePaths = "alumno")
    Optional<CuentaCredito> findByAlumno_IdAlumno(
            String idAlumno
    );

    @EntityGraph(attributePaths = {
            "alumno",
            "alumno.usuario"
    })
    Optional<CuentaCredito> findByAlumno_Usuario_IdUsuario(
            String idUsuario
    );
}