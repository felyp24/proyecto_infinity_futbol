package com.infinityfutbol.repository;

import com.infinityfutbol.entity.CuentaCredito;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT cc
        FROM CuentaCredito cc
        WHERE cc.alumno.idAlumno = :idAlumno
        """)
    Optional<CuentaCredito> buscarPorAlumnoConBloqueo(
            @Param("idAlumno")
            String idAlumno
    );


}