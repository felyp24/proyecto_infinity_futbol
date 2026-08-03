package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoClase;

import java.time.LocalDate;
import java.time.LocalTime;

public record ClaseAsistenciaResponse(

        String idClase,
        String titulo,

        LocalDate fechaClase,
        LocalTime horaInicio,
        LocalTime horaFin,

        String nombreSede,
        Integer numeroCancha,
        String nombreEntrenador,

        EstadoClase estadoClase,

        long reservasConfirmadas,
        long asistenciasRegistradas

) {
}