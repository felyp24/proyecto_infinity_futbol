package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoAsistencia;

import java.time.LocalDateTime;

public record AlumnoAsistenciaResponse(

        String idReserva,
        String idAlumno,

        String nombreCompleto,
        String username,

        String tipoDocumento,
        String numeroDocumento,

        EstadoAsistencia estadoAsistencia,
        LocalDateTime horaMarcacion,
        String observacion

) {
}