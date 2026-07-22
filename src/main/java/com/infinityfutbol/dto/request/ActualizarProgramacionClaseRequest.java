package com.infinityfutbol.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ActualizarProgramacionClaseRequest(

        @NotNull(
                message = "La fecha de la clase es obligatoria"
        )
        @FutureOrPresent(
                message = "La clase no puede programarse en una fecha pasada"
        )
        LocalDate fechaClase,

        @NotNull(
                message = "La hora de inicio es obligatoria"
        )
        LocalTime horaInicio,

        @NotNull(
                message = "La hora de finalización es obligatoria"
        )
        LocalTime horaFin,

        @NotBlank(
                message = "Debe seleccionar una cancha"
        )
        @Size(
                max = 20,
                message = "El identificador de la cancha no es válido"
        )
        String idCancha

) {
}