package com.infinityfutbol.dto.response;

import com.infinityfutbol.entity.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaResponse(

        String idReserva,
        String idClase,
        String titulo,

        LocalDate fechaClase,
        LocalTime horaInicio,
        LocalTime horaFin,

        String nombreSede,
        Integer numeroCancha,
        String nombreEntrenador,

        Integer creditosUsados,
        EstadoReserva estado,
        LocalDateTime fechaReserva,

        Integer saldoCreditos,
        Integer cuposDisponibles

) {
}