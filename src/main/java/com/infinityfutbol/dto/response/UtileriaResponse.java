package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoUtileria;

import java.time.LocalDateTime;

public record UtileriaResponse(

        String idUtileria,

        String idSede,
        String nombreSede,
        String distrito,

        String nombre,
        String categoria,
        String unidadMedida,

        Integer cantidadActual,
        Integer stockMinimo,
        Integer cantidadFaltante,

        EstadoUtileria estado,
        String situacion,

        String observacion,

        String registradoPor,
        String actualizadoPor,

        LocalDateTime fechaRegistro,
        LocalDateTime fechaActualizacion

) {
}