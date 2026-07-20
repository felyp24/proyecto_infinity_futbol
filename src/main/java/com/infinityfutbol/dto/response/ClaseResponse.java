package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoClase;

import java.time.LocalDate;
import java.time.LocalTime;

public record ClaseResponse(

        String idClase,
        String titulo,
        String descripcion,

        LocalDate fechaClase,
        LocalTime horaInicio,
        LocalTime horaFin,

        Integer cupoMaximo,
        Integer cupoDisponible,
        EstadoClase estado,

        String idCancha,
        Integer numeroCancha,
        String nombreSede,

        String idEntrenador,
        String nombreEntrenador

) {
}