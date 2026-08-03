package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.AsistenciaDetalleResponse;
import com.infinityfutbol.dto.response.ReporteAsistenciaResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Asistencia;
import com.infinityfutbol.entity.Cancha;
import com.infinityfutbol.entity.Clase;
import com.infinityfutbol.entity.Entrenador;
import com.infinityfutbol.entity.Reserva;
import com.infinityfutbol.entity.Sede;
import com.infinityfutbol.entity.enums.EstadoAsistencia;
import com.infinityfutbol.repository.AsistenciaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteAsistenciaService {

    private static final int MAXIMO_DIAS_REPORTE =
            366;

    private final AsistenciaRepository
            asistenciaRepository;

    public ReporteAsistenciaService(
            AsistenciaRepository asistenciaRepository
    ) {
        this.asistenciaRepository =
                asistenciaRepository;
    }

    public ReporteAsistenciaResponse generarReporte(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String texto
    ) {
        validarFechas(
                fechaInicio,
                fechaFin
        );

        String criterio =
                texto == null
                        ? ""
                        : texto.trim();

        List<Asistencia> registros =
                asistenciaRepository
                        .buscarParaReporte(
                                fechaInicio,
                                fechaFin,
                                criterio
                        );

        List<AsistenciaDetalleResponse> detalles =
                registros.stream()
                        .map(this::convertirDetalle)
                        .toList();

        long presentes =
                contarPorEstado(
                        registros,
                        EstadoAsistencia.PRESENTE
                );

        long tardanzas =
                contarPorEstado(
                        registros,
                        EstadoAsistencia.TARDANZA
                );

        long ausentes =
                contarPorEstado(
                        registros,
                        EstadoAsistencia.AUSENTE
                );

        long justificadas =
                contarPorEstado(
                        registros,
                        EstadoAsistencia.JUSTIFICADA
                );

        long totalRegistros =
                registros.size();

        long totalAlumnos =
                registros.stream()
                        .map(asistencia ->
                                asistencia
                                        .getReserva()
                                        .getAlumno()
                                        .getIdAlumno()
                        )
                        .distinct()
                        .count();

        /*
         * Para el porcentaje se considera que:
         *
         * PRESENTE y TARDANZA cuentan como asistencia.
         * AUSENTE y JUSTIFICADA no cuentan como asistencia.
         */
        long asistenciasEfectivas =
                presentes + tardanzas;

        BigDecimal porcentaje =
                calcularPorcentaje(
                        asistenciasEfectivas,
                        totalRegistros
                );

        return new ReporteAsistenciaResponse(
                fechaInicio,
                fechaFin,
                criterio,

                totalRegistros,
                totalAlumnos,

                presentes,
                tardanzas,
                ausentes,
                justificadas,

                porcentaje,

                detalles
        );
    }

    private long contarPorEstado(
            List<Asistencia> registros,
            EstadoAsistencia estado
    ) {
        return registros.stream()
                .filter(asistencia ->
                        asistencia.getEstadoAsistencia()
                                == estado
                )
                .count();
    }

    private BigDecimal calcularPorcentaje(
            long asistenciasEfectivas,
            long total
    ) {
        if (total == 0) {
            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        return BigDecimal
                .valueOf(asistenciasEfectivas)
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .divide(
                        BigDecimal.valueOf(total),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private AsistenciaDetalleResponse convertirDetalle(
            Asistencia asistencia
    ) {
        Reserva reserva =
                asistencia.getReserva();

        Alumno alumno =
                reserva.getAlumno();

        Clase clase =
                reserva.getClase();

        Cancha cancha =
                clase.getCancha();

        Sede sede =
                cancha.getSede();

        Entrenador entrenador =
                clase.getEntrenador();

        String nombreAlumno =
                unirNombre(
                        alumno.getNombres(),
                        alumno.getApellidos()
                );

        String nombreEntrenador =
                unirNombre(
                        entrenador.getNombres(),
                        entrenador.getApellidos()
                );

        String tipoDocumento =
                alumno.getTipoDocumento() == null
                        ? null
                        : alumno.getTipoDocumento()
                        .name();

        return new AsistenciaDetalleResponse(
                asistencia.getIdAsistencia(),
                reserva.getIdReserva(),

                clase.getFechaClase(),
                clase.getHoraInicio(),
                clase.getHoraFin(),

                alumno.getIdAlumno(),
                nombreAlumno,
                alumno.getUsuario().getUsername(),

                tipoDocumento,
                alumno.getNumeroDocumento(),

                clase.getTitulo(),
                sede.getNombre(),
                cancha.getNumeroCancha(),
                nombreEntrenador,

                asistencia.getEstadoAsistencia(),
                asistencia.getHoraMarcacion(),
                asistencia.getObservacion()
        );
    }

    private String unirNombre(
            String nombres,
            String apellidos
    ) {
        String nombre =
                nombres == null
                        ? ""
                        : nombres.trim();

        String apellido =
                apellidos == null
                        ? ""
                        : apellidos.trim();

        return (
                nombre
                        + " "
                        + apellido
        ).trim();
    }

    private void validarFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        if (
                fechaInicio == null
                        || fechaFin == null
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar la fecha inicial y la fecha final"
            );
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha inicial no puede ser posterior a la fecha final"
            );
        }

        long dias =
                ChronoUnit.DAYS.between(
                        fechaInicio,
                        fechaFin
                );

        if (dias > MAXIMO_DIAS_REPORTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El periodo no puede superar los 366 días"
            );
        }
    }
}