package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoAlumno;

import java.time.LocalDate;
import java.util.List;

public record ReporteMatriculadosResponse(

        LocalDate fechaInicio,
        LocalDate fechaFin,

        String textoBusqueda,
        EstadoAlumno estadoFiltro,

        long totalMatriculados,
        long matriculadosActivos,
        long matriculadosInactivos,

        List<MatriculadoDetalleResponse> matriculados

) {
}