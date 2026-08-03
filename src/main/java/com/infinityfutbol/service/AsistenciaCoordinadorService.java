package com.infinityfutbol.service;

import com.infinityfutbol.dto.request.GuardarAsistenciasRequest;
import com.infinityfutbol.dto.request.MarcarAsistenciaRequest;
import com.infinityfutbol.dto.response.AlumnoAsistenciaResponse;
import com.infinityfutbol.dto.response.ClaseAsistenciaResponse;
import com.infinityfutbol.dto.response.GuardarAsistenciasResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Asistencia;
import com.infinityfutbol.entity.Clase;
import com.infinityfutbol.entity.Reserva;
import com.infinityfutbol.entity.enums.EstadoAsistencia;
import com.infinityfutbol.entity.enums.EstadoClase;
import com.infinityfutbol.entity.enums.EstadoReserva;
import com.infinityfutbol.repository.AsistenciaRepository;
import com.infinityfutbol.repository.ClaseRepository;
import com.infinityfutbol.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AsistenciaCoordinadorService {

    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;
    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaCoordinadorService(
            ClaseRepository claseRepository,
            ReservaRepository reservaRepository,
            AsistenciaRepository asistenciaRepository
    ) {
        this.claseRepository =
                claseRepository;

        this.reservaRepository =
                reservaRepository;

        this.asistenciaRepository =
                asistenciaRepository;
    }

    public List<ClaseAsistenciaResponse>
    listarClases(
            LocalDate fecha
    ) {
        validarFechaConsulta(fecha);

        List<Clase> clases =
                claseRepository
                        .listarClasesParaAsistencia(
                                fecha,
                                EstadoClase.CANCELADA
                        );

        return clases.stream()
                .map(this::convertirClase)
                .toList();
    }

    public List<AlumnoAsistenciaResponse>
    listarAlumnosClase(
            String idClase
    ) {
        Clase clase =
                buscarClase(idClase);

        validarClaseMarcable(clase);

        List<Reserva> reservas =
                reservaRepository
                        .findByClase_IdClaseAndEstadoOrderByAlumno_ApellidosAscAlumno_NombresAsc(
                                idClase,
                                EstadoReserva.CONFIRMADA
                        );

        Map<String, Asistencia>
                asistenciasPorReserva =
                asistenciaRepository
                        .findByReserva_Clase_IdClase(
                                idClase
                        )
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        asistencia ->
                                                asistencia
                                                        .getReserva()
                                                        .getIdReserva(),

                                        Function.identity()
                                )
                        );

        return reservas.stream()
                .map(reserva ->
                        convertirAlumno(
                                reserva,
                                asistenciasPorReserva.get(
                                        reserva.getIdReserva()
                                )
                        )
                )
                .toList();
    }

    @Transactional
    public GuardarAsistenciasResponse
    guardarAsistencias(
            String idClase,
            GuardarAsistenciasRequest request
    ) {
        Clase clase =
                buscarClase(idClase);

        validarClaseMarcable(clase);

        List<Reserva> reservas =
                reservaRepository
                        .findByClase_IdClaseAndEstadoOrderByAlumno_ApellidosAscAlumno_NombresAsc(
                                idClase,
                                EstadoReserva.CONFIRMADA
                        );

        if (reservas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La clase no tiene alumnos con reserva confirmada"
            );
        }

        Map<String, Reserva> reservasValidas =
                reservas.stream()
                        .collect(
                                Collectors.toMap(
                                        Reserva::getIdReserva,
                                        Function.identity()
                                )
                        );

        validarSolicitudesDuplicadas(
                request.asistencias()
        );

        Map<String, Asistencia>
                asistenciasExistentes =
                asistenciaRepository
                        .findByReserva_Clase_IdClase(
                                idClase
                        )
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        asistencia ->
                                                asistencia
                                                        .getReserva()
                                                        .getIdReserva(),

                                        Function.identity()
                                )
                        );

        List<Asistencia> asistenciasGuardar =
                new ArrayList<>();

        LocalDateTime ahora =
                LocalDateTime.now();

        for (
                MarcarAsistenciaRequest item
                : request.asistencias()
        ) {
            Reserva reserva =
                    reservasValidas.get(
                            item.idReserva()
                    );

            if (reserva == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La reserva "
                                + item.idReserva()
                                + " no pertenece a la clase seleccionada"
                );
            }

            Asistencia asistencia =
                    asistenciasExistentes.get(
                            item.idReserva()
                    );

            boolean esNueva =
                    asistencia == null;

            if (esNueva) {
                asistencia =
                        new Asistencia();

                asistencia.setIdAsistencia(
                        generarIdAsistencia()
                );

                asistencia.setReserva(
                        reserva
                );
            }

            EstadoAsistencia estadoAnterior =
                    asistencia
                            .getEstadoAsistencia();

            EstadoAsistencia estadoNuevo =
                    item.estadoAsistencia();

            asistencia.setEstadoAsistencia(
                    estadoNuevo
            );

            asistencia.setObservacion(
                    limpiarObservacion(
                            item.observacion()
                    )
            );

            actualizarHoraMarcacion(
                    asistencia,
                    estadoAnterior,
                    estadoNuevo,
                    ahora,
                    esNueva
            );

            asistenciasGuardar.add(
                    asistencia
            );
        }

        asistenciaRepository.saveAll(
                asistenciasGuardar
        );

        return new GuardarAsistenciasResponse(
                idClase,
                asistenciasGuardar.size(),
                "Las asistencias se guardaron correctamente"
        );
    }

    private ClaseAsistenciaResponse convertirClase(
            Clase clase
    ) {
        long reservasConfirmadas =
                reservaRepository
                        .countByClase_IdClaseAndEstado(
                                clase.getIdClase(),
                                EstadoReserva.CONFIRMADA
                        );

        long asistenciasRegistradas =
                asistenciaRepository
                        .countByReserva_Clase_IdClase(
                                clase.getIdClase()
                        );

        String nombreEntrenador =
                (
                        clase.getEntrenador()
                                .getNombres()
                                + " "
                                + clase.getEntrenador()
                                .getApellidos()
                ).trim();

        return new ClaseAsistenciaResponse(
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

                clase.getEstado(),

                reservasConfirmadas,
                asistenciasRegistradas
        );
    }

    private AlumnoAsistenciaResponse convertirAlumno(
            Reserva reserva,
            Asistencia asistencia
    ) {
        Alumno alumno =
                reserva.getAlumno();

        String nombreCompleto =
                (
                        alumno.getNombres()
                                + " "
                                + alumno.getApellidos()
                ).trim();

        String tipoDocumento =
                alumno.getTipoDocumento() == null
                        ? null
                        : alumno.getTipoDocumento()
                        .name();

        return new AlumnoAsistenciaResponse(
                reserva.getIdReserva(),
                alumno.getIdAlumno(),

                nombreCompleto,
                alumno.getUsuario()
                        .getUsername(),

                tipoDocumento,
                alumno.getNumeroDocumento(),

                asistencia == null
                        ? null
                        : asistencia
                        .getEstadoAsistencia(),

                asistencia == null
                        ? null
                        : asistencia
                        .getHoraMarcacion(),

                asistencia == null
                        ? null
                        : asistencia
                        .getObservacion()
        );
    }

    private Clase buscarClase(
            String idClase
    ) {
        return claseRepository
                .buscarClaseParaAsistencia(
                        idClase
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe la clase seleccionada"
                        )
                );
    }

    private void validarFechaConsulta(
            LocalDate fecha
    ) {
        if (fecha == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar la fecha de las clases"
            );
        }

        if (fecha.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede registrar asistencia en fechas futuras"
            );
        }
    }

    private void validarClaseMarcable(
            Clase clase
    ) {
        if (
                clase.getEstado()
                        == EstadoClase.CANCELADA
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede registrar asistencia en una clase cancelada"
            );
        }

        if (
                clase.getFechaClase()
                        .isAfter(LocalDate.now())
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede registrar asistencia antes de la fecha de la clase"
            );
        }
    }

    private void validarSolicitudesDuplicadas(
            List<MarcarAsistenciaRequest> items
    ) {
        Set<String> identificadores =
                new HashSet<>();

        for (
                MarcarAsistenciaRequest item
                : items
        ) {
            boolean agregado =
                    identificadores.add(
                            item.idReserva()
                    );

            if (!agregado) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La reserva "
                                + item.idReserva()
                                + " se encuentra repetida"
                );
            }
        }
    }

    private void actualizarHoraMarcacion(
            Asistencia asistencia,
            EstadoAsistencia estadoAnterior,
            EstadoAsistencia estadoNuevo,
            LocalDateTime ahora,
            boolean esNueva
    ) {
        boolean registraLlegada =
                estadoNuevo
                        == EstadoAsistencia.PRESENTE
                        || estadoNuevo
                        == EstadoAsistencia.TARDANZA;

        if (!registraLlegada) {
            asistencia.setHoraMarcacion(
                    null
            );

            return;
        }

        boolean cambioEstado =
                estadoAnterior
                        != estadoNuevo;

        if (
                esNueva
                        || cambioEstado
                        || asistencia
                        .getHoraMarcacion()
                        == null
        ) {
            asistencia.setHoraMarcacion(
                    ahora
            );
        }
    }

    private String limpiarObservacion(
            String observacion
    ) {
        if (
                observacion == null
                        || observacion.isBlank()
        ) {
            return null;
        }

        return observacion.trim();
    }

    private String generarIdAsistencia() {
        return "ASI-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }
}