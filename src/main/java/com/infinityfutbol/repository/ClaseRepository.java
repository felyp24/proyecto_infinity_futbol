package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Clase;
import com.infinityfutbol.entity.enums.EstadoClase;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

import java.time.LocalTime;

import java.time.LocalDate;
import java.util.List;

public interface ClaseRepository
        extends JpaRepository<Clase, String> {

    @EntityGraph(attributePaths = {
            "cancha",
            "cancha.sede",
            "cancha.sede.distrito",
            "entrenador"
    })
    List<Clase> findByEstadoAndFechaClaseGreaterThanEqualOrderByFechaClaseAscHoraInicioAsc(
            EstadoClase estado,
            LocalDate fechaDesde
    );

    @Query("""
        SELECT COUNT(c) > 0
        FROM Clase c
        WHERE c.fechaClase = :fechaClase
          AND c.cancha.idCancha = :idCancha
          AND c.estado = :estado
          AND c.horaInicio < :horaFin
          AND c.horaFin > :horaInicio
        """)
    boolean existeCruceCancha(
            @Param("fechaClase")
            LocalDate fechaClase,

            @Param("horaInicio")
            LocalTime horaInicio,

            @Param("horaFin")
            LocalTime horaFin,

            @Param("idCancha")
            String idCancha,

            @Param("estado")
            EstadoClase estado
    );

    @Query("""
        SELECT COUNT(c) > 0
        FROM Clase c
        WHERE c.fechaClase = :fechaClase
          AND c.entrenador.idEntrenador = :idEntrenador
          AND c.estado = :estado
          AND c.horaInicio < :horaFin
          AND c.horaFin > :horaInicio
        """)
    boolean existeCruceEntrenador(
            @Param("fechaClase")
            LocalDate fechaClase,

            @Param("horaInicio")
            LocalTime horaInicio,

            @Param("horaFin")
            LocalTime horaFin,

            @Param("idEntrenador")
            String idEntrenador,

            @Param("estado")
            EstadoClase estado
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT c
        FROM Clase c
        WHERE c.idClase = :idClase
        """)
    Optional<Clase> buscarPorIdConBloqueo(
            @Param("idClase")
            String idClase
    );

    @EntityGraph(attributePaths = {
            "cancha",
            "cancha.sede",
            "cancha.sede.distrito",
            "entrenador"
    })
    List<Clase>
    findByEstadoAndFechaClaseGreaterThanEqualAndFechaClaseLessThanOrderByFechaClaseAscHoraInicioAsc(
            EstadoClase estado,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    @Query("""
        SELECT COUNT(c) > 0
        FROM Clase c
        WHERE c.idClase <> :idClase
          AND c.fechaClase = :fechaClase
          AND c.cancha.idCancha = :idCancha
          AND c.estado = :estado
          AND c.horaInicio < :horaFin
          AND c.horaFin > :horaInicio
        """)
    boolean existeCruceCanchaAlEditar(
            @Param("idClase")
            String idClase,

            @Param("fechaClase")
            LocalDate fechaClase,

            @Param("horaInicio")
            LocalTime horaInicio,

            @Param("horaFin")
            LocalTime horaFin,

            @Param("idCancha")
            String idCancha,

            @Param("estado")
            EstadoClase estado
    );

    @Query("""
        SELECT COUNT(c) > 0
        FROM Clase c
        WHERE c.idClase <> :idClase
          AND c.fechaClase = :fechaClase
          AND c.entrenador.idEntrenador = :idEntrenador
          AND c.estado = :estado
          AND c.horaInicio < :horaFin
          AND c.horaFin > :horaInicio
        """)
    boolean existeCruceEntrenadorAlEditar(
            @Param("idClase")
            String idClase,

            @Param("fechaClase")
            LocalDate fechaClase,

            @Param("horaInicio")
            LocalTime horaInicio,

            @Param("horaFin")
            LocalTime horaFin,

            @Param("idEntrenador")
            String idEntrenador,

            @Param("estado")
            EstadoClase estado
    );

    @EntityGraph(attributePaths = {
            "cancha",
            "cancha.sede",
            "entrenador"
    })
    @Query("""
    SELECT c
    FROM Clase c
    WHERE c.fechaClase = :fechaClase
      AND c.estado <> :estadoExcluido
    ORDER BY c.horaInicio ASC
    """)
    List<Clase> listarClasesParaAsistencia(
            @Param("fechaClase")
            LocalDate fechaClase,

            @Param("estadoExcluido")
            EstadoClase estadoExcluido
    );

    @EntityGraph(attributePaths = {
            "cancha",
            "cancha.sede",
            "entrenador"
    })
    @Query("""
    SELECT c
    FROM Clase c
    WHERE c.idClase = :idClase
    """)
    Optional<Clase> buscarClaseParaAsistencia(
            @Param("idClase")
            String idClase
    );
}