package com.infinityfutbol.service;

import com.infinityfutbol.entity.Clase;
import com.infinityfutbol.entity.Notificacion;
import com.infinityfutbol.entity.Reserva;
import com.infinityfutbol.entity.enums.EstadoNotificacion;
import com.infinityfutbol.entity.enums.EstadoReserva;
import com.infinityfutbol.entity.enums.TipoNotificacion;
import com.infinityfutbol.repository.NotificacionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.infinityfutbol.dto.response.NotificacionResponse;
import com.infinityfutbol.dto.response.NotificacionesInicioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class NotificacionService {

    private static final long HORAS_ANTICIPACION = 24;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(
            NotificacionRepository notificacionRepository
    ) {
        this.notificacionRepository =
                notificacionRepository;
    }

    @Transactional
    public void programarRecordatorioClase(
            Reserva reserva
    ) {
        Clase clase = reserva.getClase();

        LocalDateTime inicioClase =
                obtenerInicioClase(clase);

        Notificacion notificacion =
                notificacionRepository
                        .findByReserva_IdReservaAndTipo(
                                reserva.getIdReserva(),
                                TipoNotificacion.RECORDATORIO_CLASE
                        )
                        .orElseGet(Notificacion::new);

        if (notificacion.getIdNotificacion() == null) {
            notificacion.setIdNotificacion(
                    generarIdNotificacion()
            );

            notificacion.setAlumno(
                    reserva.getAlumno()
            );

            notificacion.setReserva(
                    reserva
            );

            notificacion.setTipo(
                    TipoNotificacion.RECORDATORIO_CLASE
            );
        }

        actualizarContenido(
                notificacion,
                clase
        );

        notificacion.setFechaProgramada(
                inicioClase.minusHours(
                        HORAS_ANTICIPACION
                )
        );

        notificacion.setFechaEnvio(null);

        notificacion.setEstado(
                EstadoNotificacion.PENDIENTE
        );

        notificacionRepository.save(
                notificacion
        );
    }

    @Transactional
    public void cancelarRecordatorioClase(
            Reserva reserva
    ) {
        notificacionRepository
                .findByReserva_IdReservaAndTipo(
                        reserva.getIdReserva(),
                        TipoNotificacion.RECORDATORIO_CLASE
                )
                .ifPresent(notificacion -> {
                    notificacion.setEstado(
                            EstadoNotificacion.CANCELADA
                    );

                    notificacion.setFechaEnvio(null);
                });
    }

    @Transactional
    public void reprogramarRecordatoriosClase(
            Clase clase
    ) {
        List<Notificacion> notificaciones =
                notificacionRepository
                        .findByReserva_Clase_IdClaseAndTipoAndReserva_Estado(
                                clase.getIdClase(),
                                TipoNotificacion.RECORDATORIO_CLASE,
                                EstadoReserva.CONFIRMADA
                        );

        LocalDateTime nuevaFechaProgramada =
                obtenerInicioClase(clase)
                        .minusHours(
                                HORAS_ANTICIPACION
                        );

        for (Notificacion notificacion : notificaciones) {
            actualizarContenido(
                    notificacion,
                    clase
            );

            notificacion.setFechaProgramada(
                    nuevaFechaProgramada
            );

            notificacion.setFechaEnvio(null);

            notificacion.setEstado(
                    EstadoNotificacion.PENDIENTE
            );
        }
    }

    @Transactional
    public int activarRecordatoriosPendientes() {
        LocalDateTime fechaActual =
                LocalDateTime.now();

        List<Notificacion> pendientes =
                notificacionRepository
                        .buscarPendientesParaEnviar(
                                EstadoNotificacion.PENDIENTE,
                                fechaActual,
                                PageRequest.of(0, 100)
                        );

        for (Notificacion notificacion : pendientes) {
            notificacion.setEstado(
                    EstadoNotificacion.ENVIADA
            );

            notificacion.setFechaEnvio(
                    fechaActual
            );
        }

        return pendientes.size();
    }

    private void actualizarContenido(
            Notificacion notificacion,
            Clase clase
    ) {
        notificacion.setTitulo(
                "Recordatorio de clase"
        );

        String mensaje =
                "Recuerda que tienes "
                        + clase.getTitulo()
                        + " el "
                        + clase.getFechaClase()
                        .format(FORMATO_FECHA)
                        + " a las "
                        + clase.getHoraInicio()
                        .format(FORMATO_HORA)
                        + " en "
                        + clase.getCancha()
                        .getSede()
                        .getNombre()
                        + ", cancha "
                        + clase.getCancha()
                        .getNumeroCancha()
                        + ".";

        notificacion.setMensaje(
                limitarTexto(
                        mensaje,
                        255
                )
        );
    }

    private LocalDateTime obtenerInicioClase(
            Clase clase
    ) {
        return LocalDateTime.of(
                clase.getFechaClase(),
                clase.getHoraInicio()
        );
    }

    private String limitarTexto(
            String texto,
            int longitudMaxima
    ) {
        if (texto.length() <= longitudMaxima) {
            return texto;
        }

        return texto.substring(
                0,
                longitudMaxima
        );
    }

    private String generarIdNotificacion() {
        return "NTF-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    @Transactional(readOnly = true)
    public NotificacionesInicioResponse listarNotificacionesCliente(
            String idUsuario
    ) {
        List<EstadoNotificacion> estadosVisibles =
                List.of(
                        EstadoNotificacion.ENVIADA,
                        EstadoNotificacion.LEIDA
                );

        List<NotificacionResponse> notificaciones =
                notificacionRepository
                        .listarNotificacionesCliente(
                                idUsuario,
                                estadosVisibles,
                                PageRequest.of(0, 20)
                        )
                        .stream()
                        .map(this::convertirResponse)
                        .toList();

        long cantidadNoLeidas =
                notificacionRepository
                        .countByAlumno_Usuario_IdUsuarioAndEstado(
                                idUsuario,
                                EstadoNotificacion.ENVIADA
                        );

        return new NotificacionesInicioResponse(
                cantidadNoLeidas,
                notificaciones
        );
    }

    @Transactional
    public NotificacionResponse marcarComoLeida(
            String idUsuario,
            String idNotificacion
    ) {
        Notificacion notificacion =
                notificacionRepository
                        .buscarNotificacionClienteConBloqueo(
                                idNotificacion,
                                idUsuario
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "La notificación no existe o no pertenece al usuario"
                                )
                        );

        if (
                notificacion.getEstado()
                        == EstadoNotificacion.PENDIENTE
                        || notificacion.getEstado()
                        == EstadoNotificacion.CANCELADA
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La notificación todavía no está disponible"
            );
        }

        if (
                notificacion.getEstado()
                        == EstadoNotificacion.ENVIADA
        ) {
            notificacion.setEstado(
                    EstadoNotificacion.LEIDA
            );
        }

        return convertirResponse(
                notificacion
        );
    }

    private NotificacionResponse convertirResponse(
            Notificacion notificacion
    ) {
        String idReserva = null;
        String idClase = null;

        if (notificacion.getReserva() != null) {
            idReserva =
                    notificacion
                            .getReserva()
                            .getIdReserva();

            if (
                    notificacion
                            .getReserva()
                            .getClase() != null
            ) {
                idClase =
                        notificacion
                                .getReserva()
                                .getClase()
                                .getIdClase();
            }
        }

        return new NotificacionResponse(
                notificacion.getIdNotificacion(),
                notificacion.getTitulo(),
                notificacion.getMensaje(),

                notificacion.getTipo(),
                notificacion.getEstado(),

                notificacion.getFechaEnvio(),

                idReserva,
                idClase
        );
    }
}