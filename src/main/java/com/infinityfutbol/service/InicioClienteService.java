package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.ClaseCalendarioResponse;
import com.infinityfutbol.entity.Clase;
import com.infinityfutbol.entity.Reserva;
import com.infinityfutbol.entity.enums.EstadoClase;
import com.infinityfutbol.entity.enums.EstadoReserva;
import com.infinityfutbol.repository.ClaseRepository;
import com.infinityfutbol.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.infinityfutbol.dto.response.ResumenInicioResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.CuentaCredito;
import com.infinityfutbol.repository.CuentaCreditoRepository;

import java.time.LocalTime;

@Service
public class InicioClienteService {

    private static final int COSTO_RESERVA = 1;

    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;
    private final CuentaCreditoRepository cuentaCreditoRepository;

    public InicioClienteService(
            ClaseRepository claseRepository,
            ReservaRepository reservaRepository,
            CuentaCreditoRepository cuentaCreditoRepository
    ) {
        this.claseRepository = claseRepository;
        this.reservaRepository = reservaRepository;
        this.cuentaCreditoRepository =
                cuentaCreditoRepository;
    }

    @Transactional(readOnly = true)
    public List<ClaseCalendarioResponse> listarClasesCalendario(
            String idUsuario,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        validarRangoFechas(
                fechaInicio,
                fechaFin
        );

        LocalDate fechaDesde =
                fechaInicio.isBefore(LocalDate.now())
                        ? LocalDate.now()
                        : fechaInicio;

        if (!fechaFin.isAfter(fechaDesde)) {
            return List.of();
        }

        List<Clase> clases =
                claseRepository
                        .findByEstadoAndFechaClaseGreaterThanEqualAndFechaClaseLessThanOrderByFechaClaseAscHoraInicioAsc(
                                EstadoClase.PROGRAMADA,
                                fechaDesde,
                                fechaFin
                        );

        List<Reserva> reservas =
                reservaRepository
                        .findByAlumno_Usuario_IdUsuarioAndEstadoAndClase_FechaClaseGreaterThanEqualAndClase_FechaClaseLessThan(
                                idUsuario,
                                EstadoReserva.CONFIRMADA,
                                fechaDesde,
                                fechaFin
                        );

        Set<String> clasesReservadas =
                reservas.stream()
                        .map(reserva ->
                                reserva.getClase().getIdClase()
                        )
                        .collect(Collectors.toSet());

        LocalDateTime ahora =
                LocalDateTime.now();

        return clases.stream()
                .map(clase ->
                        convertirClaseCalendario(
                                clase,
                                clasesReservadas,
                                ahora
                        )
                )
                .toList();
    }

    private void validarRangoFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        if (
                fechaInicio == null
                        || fechaFin == null
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar el rango de fechas del calendario"
            );
        }

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha final debe ser posterior a la fecha inicial"
            );
        }

        if (fechaInicio.plusMonths(3).isBefore(fechaFin)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El rango consultado no puede superar los tres meses"
            );
        }
    }

    private ClaseCalendarioResponse convertirClaseCalendario(
            Clase clase,
            Set<String> clasesReservadas,
            LocalDateTime ahora
    ) {
        LocalDateTime inicio =
                LocalDateTime.of(
                        clase.getFechaClase(),
                        clase.getHoraInicio()
                );

        LocalDateTime fin =
                LocalDateTime.of(
                        clase.getFechaClase(),
                        clase.getHoraFin()
                );

        boolean reservada =
                clasesReservadas.contains(
                        clase.getIdClase()
                );

        boolean tieneCupos =
                clase.getCupoDisponible() != null
                        && clase.getCupoDisponible() > 0;

        boolean todaviaNoComienza =
                inicio.isAfter(ahora);

        boolean disponible =
                !reservada
                        && tieneCupos
                        && todaviaNoComienza;

        String situacion =
                determinarSituacion(
                        reservada,
                        tieneCupos,
                        todaviaNoComienza
                );

        String nombreEntrenador =
                clase.getEntrenador().getNombres()
                        + " "
                        + clase.getEntrenador().getApellidos();

        return new ClaseCalendarioResponse(
                clase.getIdClase(),
                clase.getTitulo(),

                inicio,
                fin,

                clase.getDescripcion(),

                clase.getCancha()
                        .getSede()
                        .getNombre(),

                clase.getCancha()
                        .getSede()
                        .getDistrito()
                        .getNombre(),

                clase.getCancha()
                        .getNumeroCancha(),

                clase.getCancha()
                        .getTipoSuperficie(),

                nombreEntrenador,

                clase.getCupoMaximo(),
                clase.getCupoDisponible(),
                COSTO_RESERVA,

                reservada,
                disponible,
                situacion
        );
    }

    private String determinarSituacion(
            boolean reservada,
            boolean tieneCupos,
            boolean todaviaNoComienza
    ) {
        if (reservada) {
            return "RESERVADA";
        }

        if (!todaviaNoComienza) {
            return "INICIADA";
        }

        if (!tieneCupos) {
            return "SIN_CUPOS";
        }

        return "DISPONIBLE";
    }

    @Transactional(readOnly = true)
    public ResumenInicioResponse obtenerResumen(
            String idUsuario
    ) {
        CuentaCredito cuentaCredito =
                cuentaCreditoRepository
                        .findByAlumno_Usuario_IdUsuario(
                                idUsuario
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "El usuario no tiene una cuenta de créditos"
                                )
                        );

        Alumno alumno =
                cuentaCredito.getAlumno();

        String nombreCompleto =
                (
                        alumno.getNombres()
                                + " "
                                + alumno.getApellidos()
                ).trim();

        long reservasProximas =
                reservaRepository
                        .contarReservasProximas(
                                idUsuario,
                                EstadoReserva.CONFIRMADA,
                                LocalDate.now(),
                                LocalTime.now()
                        );

        Integer saldoActual =
                cuentaCredito.getSaldoActual() != null
                        ? cuentaCredito.getSaldoActual()
                        : 0;

        return new ResumenInicioResponse(
                nombreCompleto,
                saldoActual,
                reservasProximas
        );
    }
}