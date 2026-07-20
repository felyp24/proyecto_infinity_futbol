package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Reserva;
import com.infinityfutbol.entity.enums.EstadoReserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalTime;

public interface ReservaRepository
        extends JpaRepository<Reserva, String> {

    boolean existsByAlumno_IdAlumnoAndClase_IdClaseAndEstado(
            String idAlumno,
            String idClase,
            EstadoReserva estado
    );

    Optional<Reserva> findByAlumno_IdAlumnoAndClase_IdClase(
            String idAlumno,
            String idClase
    );

    @EntityGraph(attributePaths = {
            "clase",
            "clase.cancha",
            "clase.cancha.sede",
            "clase.entrenador"
    })
    List<Reserva> findByAlumno_Usuario_IdUsuarioAndEstadoOrderByClase_FechaClaseAscClase_HoraInicioAsc(
            String idUsuario,
            EstadoReserva estado
    );

    @EntityGraph(attributePaths = {
            "alumno",
            "clase",
            "clase.cancha",
            "clase.cancha.sede",
            "clase.entrenador"
    })
    Optional<Reserva> findByIdReservaAndAlumno_Usuario_IdUsuario(
            String idReserva,
            String idUsuario
    );

    @EntityGraph(attributePaths = {
            "clase"
    })
    List<Reserva>
    findByAlumno_Usuario_IdUsuarioAndEstadoAndClase_FechaClaseGreaterThanEqualAndClase_FechaClaseLessThan(
            String idUsuario,
            EstadoReserva estado,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    @Query("""
        SELECT COUNT(r)
        FROM Reserva r
        WHERE r.alumno.usuario.idUsuario = :idUsuario
          AND r.estado = :estado
          AND (
                r.clase.fechaClase > :fechaActual
                OR (
                    r.clase.fechaClase = :fechaActual
                    AND r.clase.horaInicio > :horaActual
                )
          )
        """)
    long contarReservasProximas(
            @Param("idUsuario")
            String idUsuario,

            @Param("estado")
            EstadoReserva estado,

            @Param("fechaActual")
            LocalDate fechaActual,

            @Param("horaActual")
            LocalTime horaActual
    );

    @EntityGraph(attributePaths = {
            "clase",
            "clase.cancha",
            "clase.cancha.sede",
            "clase.cancha.sede.distrito",
            "clase.entrenador"
    })
    @Query("""
        SELECT r
        FROM Reserva r
        WHERE r.alumno.usuario.idUsuario = :idUsuario
          AND r.estado = :estado
          AND (
                r.clase.fechaClase > :fechaActual
                OR (
                    r.clase.fechaClase = :fechaActual
                    AND r.clase.horaFin > :horaActual
                )
          )
        ORDER BY
            r.clase.fechaClase ASC,
            r.clase.horaInicio ASC
        """)
    List<Reserva> listarReservasProximas(
            @Param("idUsuario")
            String idUsuario,

            @Param("estado")
            EstadoReserva estado,

            @Param("fechaActual")
            LocalDate fechaActual,

            @Param("horaActual")
            LocalTime horaActual
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r
        FROM Reserva r
        WHERE r.idReserva = :idReserva
          AND r.alumno.usuario.idUsuario = :idUsuario
        """)
    Optional<Reserva> buscarReservaClienteConBloqueo(
            @Param("idReserva")
            String idReserva,

            @Param("idUsuario")
            String idUsuario
    );
}