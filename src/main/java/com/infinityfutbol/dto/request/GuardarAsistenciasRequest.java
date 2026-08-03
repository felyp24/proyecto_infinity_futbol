package com.infinityfutbol.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GuardarAsistenciasRequest(

        @NotEmpty(
                message =
                        "Debe registrar al menos una asistencia"
        )
        List<
                @Valid
                        MarcarAsistenciaRequest
                > asistencias

) {
}