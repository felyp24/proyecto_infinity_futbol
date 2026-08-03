package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoAsistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AsistenciaDetalleResponse(

        String idAsistencia,
        String idReserva,

        LocalDate fechaClase,
        LocalTime horaInicio,
        LocalTime horaFin,

        String idAlumno,
        String nombreAlumno,
        String username,

        String tipoDocumento,
        String numeroDocumento,

        String tituloClase,
        String nombreSede,
        Integer numeroCancha,
        String nombreEntrenador,

        EstadoAsistencia estadoAsistencia,
        LocalDateTime horaMarcacion,
        String observacion

) {
}