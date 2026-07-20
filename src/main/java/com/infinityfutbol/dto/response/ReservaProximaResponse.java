package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaProximaResponse(

        String idReserva,
        String idClase,
        String titulo,

        LocalDate fechaClase,
        LocalTime horaInicio,
        LocalTime horaFin,

        String nombreSede,
        String distrito,
        Integer numeroCancha,

        String nombreEntrenador,

        Integer creditosUsados,
        EstadoReserva estado

) {
}