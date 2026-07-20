package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.ReservaResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Clase;
import com.infinityfutbol.entity.CuentaCredito;
import com.infinityfutbol.entity.MovimientoCredito;
import com.infinityfutbol.entity.Reserva;
import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.entity.enums.EstadoClase;
import com.infinityfutbol.entity.enums.EstadoReserva;
import com.infinityfutbol.entity.enums.TipoMovimientoCredito;
import com.infinityfutbol.repository.AlumnoRepository;
import com.infinityfutbol.repository.ClaseRepository;
import com.infinityfutbol.repository.CuentaCreditoRepository;
import com.infinityfutbol.repository.MovimientoCreditoRepository;
import com.infinityfutbol.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservaService {

    private static final int COSTO_RESERVA = 1;

    private final AlumnoRepository alumnoRepository;
    private final ClaseRepository claseRepository;
    private final CuentaCreditoRepository cuentaCreditoRepository;
    private final ReservaRepository reservaRepository;
    private final MovimientoCreditoRepository movimientoCreditoRepository;

    public ReservaService(
            AlumnoRepository alumnoRepository,
            ClaseRepository claseRepository,
            CuentaCreditoRepository cuentaCreditoRepository,
            ReservaRepository reservaRepository,
            MovimientoCreditoRepository movimientoCreditoRepository
    ) {
        this.alumnoRepository = alumnoRepository;
        this.claseRepository = claseRepository;
        this.cuentaCreditoRepository = cuentaCreditoRepository;
        this.reservaRepository = reservaRepository;
        this.movimientoCreditoRepository =
                movimientoCreditoRepository;
    }

    @Transactional
    public ReservaResponse reservarClase(
            String idUsuario,
            String idClase
    ) {
        Alumno alumno = buscarAlumnoActivo(idUsuario);

        /*
         * La clase se bloquea para evitar que dos alumnos
         * ocupen simultáneamente el último cupo.
         */
        Clase clase = claseRepository
                .buscarPorIdConBloqueo(idClase)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La clase seleccionada no existe"
                        )
                );

        validarClaseReservable(clase);

        /*
         * Se busca después de bloquear la clase para controlar
         * también los dobles clics o solicitudes simultáneas.
         */
        Optional<Reserva> reservaAnterior =
                reservaRepository
                        .findByAlumno_IdAlumnoAndClase_IdClase(
                                alumno.getIdAlumno(),
                                clase.getIdClase()
                        );

        validarReservaDuplicada(reservaAnterior);

        /*
         * La cuenta se bloquea para impedir que dos reservas
         * gasten el mismo crédito.
         */
        CuentaCredito cuentaCredito =
                cuentaCreditoRepository
                        .buscarPorAlumnoConBloqueo(
                                alumno.getIdAlumno()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "El alumno no tiene una cuenta de créditos"
                                )
                        );

        validarSaldo(cuentaCredito);

        LocalDateTime fechaActual =
                LocalDateTime.now();

        Reserva reserva = prepararReserva(
                reservaAnterior,
                alumno,
                clase,
                fechaActual
        );

        /*
         * Se guarda primero para que movimiento_credito
         * pueda referenciar correctamente la reserva.
         */
        Reserva reservaGuardada =
                reservaRepository.save(reserva);

        descontarCredito(cuentaCredito);
        descontarCupo(clase);

        cuentaCreditoRepository.save(cuentaCredito);
        claseRepository.save(clase);

        MovimientoCredito movimiento =
                crearMovimientoConsumo(
                        cuentaCredito,
                        reservaGuardada,
                        clase
                );

        movimientoCreditoRepository.save(movimiento);

        return convertirResponse(
                reservaGuardada,
                cuentaCredito,
                clase
        );
    }

    private Alumno buscarAlumnoActivo(
            String idUsuario
    ) {
        Alumno alumno = alumnoRepository
                .findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "El usuario autenticado no tiene un perfil de alumno"
                        )
                );

        if (alumno.getEstado() != EstadoAlumno.ACTIVO) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El perfil del alumno se encuentra inactivo"
            );
        }

        return alumno;
    }

    private void validarClaseReservable(
            Clase clase
    ) {
        if (clase.getEstado() != EstadoClase.PROGRAMADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La clase ya no se encuentra disponible para reservas"
            );
        }

        LocalDateTime inicioClase =
                LocalDateTime.of(
                        clase.getFechaClase(),
                        clase.getHoraInicio()
                );

        if (!inicioClase.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede reservar una clase que ya comenzó"
            );
        }

        if (
                clase.getCupoDisponible() == null
                        || clase.getCupoDisponible() <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La clase ya no tiene cupos disponibles"
            );
        }
    }

    private void validarReservaDuplicada(
            Optional<Reserva> reservaAnterior
    ) {
        boolean yaEstaConfirmada =
                reservaAnterior.isPresent()
                        && reservaAnterior.get().getEstado()
                        == EstadoReserva.CONFIRMADA;

        if (yaEstaConfirmada) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya tienes una reserva confirmada para esta clase"
            );
        }
    }

    private void validarSaldo(
            CuentaCredito cuentaCredito
    ) {
        if (
                cuentaCredito.getSaldoActual() == null
                        || cuentaCredito.getSaldoActual()
                        < COSTO_RESERVA
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No tienes créditos suficientes para reservar esta clase"
            );
        }
    }

    private Reserva prepararReserva(
            Optional<Reserva> reservaAnterior,
            Alumno alumno,
            Clase clase,
            LocalDateTime fechaActual
    ) {
        /*
         * Si existía una reserva CANCELADA, se reutiliza.
         * Esto es necesario porque MySQL no permite dos filas
         * para el mismo alumno y la misma clase.
         */
        Reserva reserva = reservaAnterior
                .orElseGet(Reserva::new);

        if (reserva.getIdReserva() == null) {
            reserva.setIdReserva(
                    generarIdReserva()
            );

            reserva.setAlumno(alumno);
            reserva.setClase(clase);
        }

        reserva.setFechaReserva(fechaActual);
        reserva.setCreditosUsados(COSTO_RESERVA);
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        return reserva;
    }

    private void descontarCredito(
            CuentaCredito cuentaCredito
    ) {
        cuentaCredito.setSaldoActual(
                cuentaCredito.getSaldoActual()
                        - COSTO_RESERVA
        );
    }

    private void descontarCupo(
            Clase clase
    ) {
        clase.setCupoDisponible(
                clase.getCupoDisponible() - 1
        );
    }

    private MovimientoCredito crearMovimientoConsumo(
            CuentaCredito cuentaCredito,
            Reserva reserva,
            Clase clase
    ) {
        MovimientoCredito movimiento =
                new MovimientoCredito();

        movimiento.setIdMovimientoCredito(
                generarIdMovimiento()
        );

        movimiento.setCuentaCredito(cuentaCredito);
        movimiento.setReserva(reserva);

        movimiento.setTipoMovimiento(
                TipoMovimientoCredito.CONSUMO
        );

        movimiento.setCantidad(-COSTO_RESERVA);

        movimiento.setDescripcion(
                "Consumo de 1 crédito por reserva de la clase: "
                        + clase.getTitulo()
        );

        return movimiento;
    }

    private ReservaResponse convertirResponse(
            Reserva reserva,
            CuentaCredito cuentaCredito,
            Clase clase
    ) {
        String nombreEntrenador =
                clase.getEntrenador().getNombres()
                        + " "
                        + clase.getEntrenador().getApellidos();

        return new ReservaResponse(
                reserva.getIdReserva(),
                clase.getIdClase(),
                clase.getTitulo(),

                clase.getFechaClase(),
                clase.getHoraInicio(),
                clase.getHoraFin(),

                clase.getCancha()
                        .getSede()
                        .getNombre(),

                clase.getCancha()
                        .getNumeroCancha(),

                nombreEntrenador,

                reserva.getCreditosUsados(),
                reserva.getEstado(),
                reserva.getFechaReserva(),

                cuentaCredito.getSaldoActual(),
                clase.getCupoDisponible()
        );
    }

    private String generarIdReserva() {
        return "RSV-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    private String generarIdMovimiento() {
        return "MOV-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }
}