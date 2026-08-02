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
import com.infinityfutbol.dto.response.ReservaProximaResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.infinityfutbol.dto.response.ReservaHistorialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ReservaService {

    private static final int COSTO_RESERVA = 1;

    private final AlumnoRepository alumnoRepository;
    private final ClaseRepository claseRepository;
    private final CuentaCreditoRepository cuentaCreditoRepository;
    private final ReservaRepository reservaRepository;
    private final MovimientoCreditoRepository movimientoCreditoRepository;
    private final NotificacionService notificacionService;

    public ReservaService(
            AlumnoRepository alumnoRepository,
            ClaseRepository claseRepository,
            CuentaCreditoRepository cuentaCreditoRepository,
            ReservaRepository reservaRepository,
            MovimientoCreditoRepository movimientoCreditoRepository,
            NotificacionService notificacionService
    ) {
        this.alumnoRepository = alumnoRepository;
        this.claseRepository = claseRepository;
        this.cuentaCreditoRepository = cuentaCreditoRepository;
        this.reservaRepository = reservaRepository;
        this.movimientoCreditoRepository =
                movimientoCreditoRepository;
        this.notificacionService =
                notificacionService;
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

        notificacionService.programarRecordatorioClase(
                reservaGuardada
        );

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

    @Transactional(readOnly = true)
    public List<ReservaProximaResponse> listarReservasProximas(
            String idUsuario
    ) {
        return reservaRepository
                .listarReservasProximas(
                        idUsuario,
                        EstadoReserva.CONFIRMADA,
                        LocalDate.now(),
                        LocalTime.now()
                )
                .stream()
                .map(this::convertirReservaProxima)
                .toList();
    }

    private ReservaProximaResponse convertirReservaProxima(
            Reserva reserva
    ) {
        Clase clase = reserva.getClase();

        String nombreEntrenador =
                clase.getEntrenador().getNombres()
                        + " "
                        + clase.getEntrenador().getApellidos();

        return new ReservaProximaResponse(
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
                        .getSede()
                        .getDistrito()
                        .getNombre(),

                clase.getCancha()
                        .getNumeroCancha(),

                nombreEntrenador,

                reserva.getCreditosUsados(),
                reserva.getEstado()
        );
    }

    @Transactional(readOnly = true)
    public Page<ReservaHistorialResponse>
    listarReservasPasadas(
            String idUsuario,
            Pageable pageable
    ) {
        return reservaRepository
                .listarReservasPasadas(
                        idUsuario,
                        LocalDate.now(),
                        LocalTime.now(),
                        pageable
                )
                .map(
                        this::convertirReservaHistorial
                );
    }

    private ReservaHistorialResponse
    convertirReservaHistorial(
            Reserva reserva
    ) {
        Clase clase =
                reserva.getClase();

        String nombreEntrenador =
                (
                        clase.getEntrenador()
                                .getNombres()
                                + " "
                                + clase.getEntrenador()
                                .getApellidos()
                ).trim();

        String situacion =
                determinarSituacionHistorial(
                        reserva,
                        clase
                );

        return new ReservaHistorialResponse(
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
                        .getSede()
                        .getDistrito()
                        .getNombre(),

                clase.getCancha()
                        .getNumeroCancha(),

                nombreEntrenador,

                reserva.getCreditosUsados(),
                reserva.getEstado(),

                situacion
        );
    }

    private String determinarSituacionHistorial(
            Reserva reserva,
            Clase clase
    ) {
        if (
                reserva.getEstado()
                        == EstadoReserva.CANCELADA
        ) {
            return "RESERVA_CANCELADA";
        }

        if (
                clase.getEstado()
                        == EstadoClase.CANCELADA
        ) {
            return "CLASE_CANCELADA";
        }

        return "FINALIZADA";
    }

    @Transactional
    public ReservaResponse cancelarReserva(
            String idUsuario,
            String idReserva
    ) {
        Reserva reserva =
                reservaRepository
                        .buscarReservaClienteConBloqueo(
                                idReserva,
                                idUsuario
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "La reserva no existe o no pertenece al usuario"
                                )
                        );

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La reserva ya se encuentra cancelada"
            );
        }

        Clase clase =
                claseRepository
                        .buscarPorIdConBloqueo(
                                reserva.getClase().getIdClase()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "La clase asociada ya no existe"
                                )
                        );

        validarCancelacion(clase);

        CuentaCredito cuentaCredito =
                cuentaCreditoRepository
                        .buscarPorAlumnoConBloqueo(
                                reserva.getAlumno().getIdAlumno()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "El alumno no tiene una cuenta de créditos"
                                )
                        );

        int creditosDevueltos =
                reserva.getCreditosUsados() != null
                        ? reserva.getCreditosUsados()
                        : COSTO_RESERVA;

        reserva.setEstado(
                EstadoReserva.CANCELADA
        );

        cuentaCredito.setSaldoActual(
                cuentaCredito.getSaldoActual()
                        + creditosDevueltos
        );

        clase.setCupoDisponible(
                clase.getCupoDisponible() + 1
        );

        reservaRepository.save(reserva);
        cuentaCreditoRepository.save(cuentaCredito);
        claseRepository.save(clase);

        MovimientoCredito movimiento =
                crearMovimientoDevolucion(
                        cuentaCredito,
                        reserva,
                        clase,
                        creditosDevueltos
                );

        movimientoCreditoRepository.save(movimiento);

        notificacionService.cancelarRecordatorioClase(
                reserva
        );
        return convertirResponse(
                reserva,
                cuentaCredito,
                clase
        );
    }

    private void validarCancelacion(
            Clase clase
    ) {
        LocalDateTime inicioClase =
                LocalDateTime.of(
                        clase.getFechaClase(),
                        clase.getHoraInicio()
                );

        if (!inicioClase.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede cancelar una reserva después de iniciada la clase"
            );
        }

        if (
                clase.getCupoDisponible() == null
                        || clase.getCupoMaximo() == null
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La información de cupos de la clase no es válida"
            );
        }

        if (
                clase.getCupoDisponible()
                        >= clase.getCupoMaximo()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo restablecer el cupo de la clase"
            );
        }
    }

    private MovimientoCredito crearMovimientoDevolucion(
            CuentaCredito cuentaCredito,
            Reserva reserva,
            Clase clase,
            int creditosDevueltos
    ) {
        MovimientoCredito movimiento =
                new MovimientoCredito();

        movimiento.setIdMovimientoCredito(
                generarIdMovimiento()
        );

        movimiento.setCuentaCredito(
                cuentaCredito
        );

        movimiento.setReserva(
                reserva
        );

        movimiento.setTipoMovimiento(
                TipoMovimientoCredito.DEVOLUCION
        );

        movimiento.setCantidad(
                creditosDevueltos
        );

        movimiento.setDescripcion(
                "Devolución de "
                        + creditosDevueltos
                        + (
                        creditosDevueltos == 1
                                ? " crédito"
                                : " créditos"
                )
                        + " por cancelación de la clase: "
                        + clase.getTitulo()
        );

        return movimiento;
    }
}