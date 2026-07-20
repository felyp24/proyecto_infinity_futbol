package com.infinityfutbol.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CrearClaseRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(
                max = 100,
                message = "El título no puede superar los 100 caracteres"
        )
        String titulo,

        @Size(
                max = 255,
                message = "La descripción no puede superar los 255 caracteres"
        )
        String descripcion,

        @NotNull(message = "La fecha de la clase es obligatoria")
        @FutureOrPresent(
                message = "La clase no puede programarse en una fecha pasada"
        )
        LocalDate fechaClase,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de finalización es obligatoria")
        LocalTime horaFin,

        @NotNull(message = "El cupo máximo es obligatorio")
        @Min(
                value = 1,
                message = "El cupo máximo debe ser mayor que cero"
        )
        @Max(
                value = 100,
                message = "El cupo máximo no puede superar las 100 personas"
        )
        Integer cupoMaximo,

        @NotBlank(message = "Debe seleccionar una cancha")
        String idCancha,

        @NotBlank(message = "Debe seleccionar un entrenador")
        String idEntrenador

) {
}