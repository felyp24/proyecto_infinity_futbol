package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Notificacion;
import com.infinityfutbol.entity.enums.EstadoNotificacion;
import com.infinityfutbol.entity.enums.EstadoReserva;
import com.infinityfutbol.entity.enums.TipoNotificacion;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificacionRepository
        extends JpaRepository<Notificacion, String> {

    Optional<Notificacion>
    findByReserva_IdReservaAndTipo(
            String idReserva,
            TipoNotificacion tipo
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "alumno",
            "reserva",
            "reserva.clase"
    })
    @Query("""
        SELECT n
        FROM Notificacion n
        WHERE n.estado = :estado
          AND n.fechaProgramada IS NOT NULL
          AND n.fechaProgramada <= :fechaActual
        ORDER BY n.fechaProgramada ASC
        """)
    List<Notificacion> buscarPendientesParaEnviar(
            @Param("estado")
            EstadoNotificacion estado,

            @Param("fechaActual")
            LocalDateTime fechaActual,

            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "reserva",
            "reserva.clase"
    })
    List<Notificacion>
    findByReserva_Clase_IdClaseAndTipoAndReserva_Estado(
            String idClase,
            TipoNotificacion tipo,
            EstadoReserva estadoReserva
    );

    @EntityGraph(attributePaths = {
            "reserva",
            "reserva.clase"
    })
    @Query("""
        SELECT n
        FROM Notificacion n
        WHERE n.alumno.usuario.idUsuario = :idUsuario
          AND n.estado IN :estados
        ORDER BY
            n.fechaEnvio DESC,
            n.fechaProgramada DESC
        """)
    List<Notificacion> listarNotificacionesCliente(
            @Param("idUsuario")
            String idUsuario,

            @Param("estados")
            List<EstadoNotificacion> estados,

            Pageable pageable
    );

    long countByAlumno_Usuario_IdUsuarioAndEstado(
            String idUsuario,
            EstadoNotificacion estado
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "reserva",
            "reserva.clase"
    })
    @Query("""
        SELECT n
        FROM Notificacion n
        WHERE n.idNotificacion = :idNotificacion
          AND n.alumno.usuario.idUsuario = :idUsuario
        """)
    Optional<Notificacion> buscarNotificacionClienteConBloqueo(
            @Param("idNotificacion")
            String idNotificacion,

            @Param("idUsuario")
            String idUsuario
    );
}