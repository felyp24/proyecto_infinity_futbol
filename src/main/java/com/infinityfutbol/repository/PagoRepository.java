package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Pago;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import com.infinityfutbol.entity.enums.EstadoPago;
import java.time.LocalDateTime;

public interface PagoRepository
        extends JpaRepository<Pago, String> {

    @EntityGraph(attributePaths = {
            "alumno",
            "paqueteCredito"
    })
    List<Pago>
    findByAlumno_Usuario_IdUsuarioOrderByFechaPagoDesc(
            String idUsuario,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "alumno",
            "paqueteCredito"
    })
    Optional<Pago>
    findByIdPagoAndAlumno_Usuario_IdUsuario(
            String idPago,
            String idUsuario
    );

    Optional<Pago> findByIdPagoExterno(
            String idPagoExterno
    );

    Optional<Pago> findByIdPreferenciaExterna(
            String idPreferenciaExterna
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "alumno",
            "paqueteCredito"
    })
    @Query("""
        SELECT p
        FROM Pago p
        WHERE p.idPago = :idPago
        """)
    Optional<Pago> buscarPorIdConBloqueo(
            @Param("idPago")
            String idPago
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "alumno",
            "paqueteCredito"
    })
    @Query("""
        SELECT p
        FROM Pago p
        WHERE p.idPago = :idPago
          AND p.alumno.usuario.idUsuario = :idUsuario
        """)
    Optional<Pago> buscarPagoClienteConBloqueo(
            @Param("idPago")
            String idPago,

            @Param("idUsuario")
            String idUsuario
    );

    @EntityGraph(attributePaths = {
            "alumno",
            "alumno.usuario",
            "paqueteCredito"
    })
    @Query("""
    SELECT p
    FROM Pago p
    WHERE p.estadoPago = :estadoPago
      AND p.fechaAprobacion IS NOT NULL
      AND p.fechaAprobacion >= :fechaInicio
      AND p.fechaAprobacion < :fechaFinExclusiva
    ORDER BY p.fechaAprobacion ASC
    """)
    List<Pago> listarIngresosAprobados(
            @Param("estadoPago")
            EstadoPago estadoPago,

            @Param("fechaInicio")
            LocalDateTime fechaInicio,

            @Param("fechaFinExclusiva")
            LocalDateTime fechaFinExclusiva
    );
}