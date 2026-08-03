package com.infinityfutbol.dto.request;

import com.infinityfutbol.entity.enums.EstadoAsistencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MarcarAsistenciaRequest(

        @NotBlank(
                message =
                        "El identificador de la reserva es obligatorio"
        )
        String idReserva,

        @NotNull(
                message =
                        "Debe seleccionar un estado de asistencia"
        )
        EstadoAsistencia estadoAsistencia,

        @Size(
                max = 255,
                message =
                        "La observación no puede superar los 255 caracteres"
        )
        String observacion

) {
}