package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.MatriculadoDetalleResponse;
import com.infinityfutbol.dto.response.ReporteMatriculadosResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.repository.AlumnoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteMatriculadosService {

    private final AlumnoRepository alumnoRepository;

    public ReporteMatriculadosService(
            AlumnoRepository alumnoRepository
    ) {
        this.alumnoRepository =
                alumnoRepository;
    }

    public ReporteMatriculadosResponse generarReporte(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String texto,
            EstadoUsuario estado
    ) {
        validarFechas(
                fechaInicio,
                fechaFin
        );

        String criterio =
                texto == null
                        ? ""
                        : texto.trim();

        LocalDateTime inicio =
                fechaInicio.atStartOfDay();

        /*
         * La fecha final es inclusiva para el usuario.
         * Internamente se consulta hasta antes del día siguiente.
         */
        LocalDateTime finExclusivo =
                fechaFin
                        .plusDays(1)
                        .atStartOfDay();

        List<Alumno> alumnos =
                alumnoRepository
                        .buscarParaReporteMatriculados(
                                inicio,
                                finExclusivo,
                                criterio,
                                estado
                        );

        List<MatriculadoDetalleResponse> detalles =
                alumnos.stream()
                        .map(this::convertirDetalle)
                        .toList();

        long activos =
                alumnos.stream()
                        .filter(alumno ->
                                alumno
                                        .getUsuario()
                                        .getEstado()
                                        == EstadoUsuario.ACTIVO
                        )
                        .count();

        long inactivos =
                alumnos.stream()
                        .filter(alumno ->
                                alumno
                                        .getUsuario()
                                        .getEstado()
                                        == EstadoUsuario.INACTIVO
                        )
                        .count();
        return new ReporteMatriculadosResponse(
                fechaInicio,
                fechaFin,

                criterio,
                estado,

                alumnos.size(),
                activos,
                inactivos,

                detalles
        );
    }

    private MatriculadoDetalleResponse convertirDetalle(
            Alumno alumno
    ) {
        Usuario usuario =
                alumno.getUsuario();

        String nombreCompleto =
                unirTexto(
                        alumno.getNombres(),
                        alumno.getApellidos()
                );

        return new MatriculadoDetalleResponse(
                alumno.getIdAlumno(),
                alumno.getFechaRegistro(),

                alumno.getNombres(),
                alumno.getApellidos(),
                nombreCompleto,

                alumno.getTipoDocumento(),
                alumno.getNumeroDocumento(),

                alumno.getFechaNacimiento(),
                alumno.getTelefono(),

                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getCorreo(),

                alumno.getEstado(),
                usuario.getEstado()
        );
    }

    private String unirTexto(
            String primerTexto,
            String segundoTexto
    ) {
        String primero =
                primerTexto == null
                        ? ""
                        : primerTexto.trim();

        String segundo =
                segundoTexto == null
                        ? ""
                        : segundoTexto.trim();

        return (
                primero
                        + " "
                        + segundo
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
    }
}