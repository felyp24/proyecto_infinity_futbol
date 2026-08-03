package com.infinityfutbol.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteAsistenciaResponse(

        LocalDate fechaInicio,
        LocalDate fechaFin,
        String textoBusqueda,

        Long totalRegistros,
        Long totalAlumnos,

        Long presentes,
        Long tardanzas,
        Long ausentes,
        Long justificadas,

        BigDecimal porcentajeAsistencia,

        List<AsistenciaDetalleResponse> asistencias

) {
}