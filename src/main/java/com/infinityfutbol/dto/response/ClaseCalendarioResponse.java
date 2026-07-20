package com.infinityfutbol.dto.response;

import java.time.LocalDateTime;

public record ClaseCalendarioResponse(

        String id,
        String title,

        LocalDateTime start,
        LocalDateTime end,

        String descripcion,

        String nombreSede,
        String distrito,
        Integer numeroCancha,
        String tipoSuperficie,

        String nombreEntrenador,

        Integer cupoMaximo,
        Integer cupoDisponible,
        Integer costoCreditos,

        boolean reservada,
        boolean disponible,
        String situacion

) {
}