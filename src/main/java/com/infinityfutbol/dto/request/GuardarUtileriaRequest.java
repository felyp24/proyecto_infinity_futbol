package com.infinityfutbol.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuardarUtileriaRequest(

        @NotBlank(
                message =
                        "Debe seleccionar una sede"
        )
        @Size(max = 20)
        String idSede,

        @NotBlank(
                message =
                        "El nombre del implemento es obligatorio"
        )
        @Size(
                max = 100,
                message =
                        "El nombre no puede superar los 100 caracteres"
        )
        String nombre,

        @NotBlank(
                message =
                        "La categoría es obligatoria"
        )
        @Size(max = 50)
        String categoria,

        @NotBlank(
                message =
                        "La unidad de medida es obligatoria"
        )
        @Size(max = 30)
        String unidadMedida,

        @NotNull(
                message =
                        "La cantidad actual es obligatoria"
        )
        @Min(
                value = 0,
                message =
                        "La cantidad actual no puede ser negativa"
        )
        @Max(
                value = 100000,
                message =
                        "La cantidad actual es demasiado alta"
        )
        Integer cantidadActual,

        @NotNull(
                message =
                        "El stock mínimo es obligatorio"
        )
        @Min(
                value = 0,
                message =
                        "El stock mínimo no puede ser negativo"
        )
        @Max(
                value = 100000,
                message =
                        "El stock mínimo es demasiado alto"
        )
        Integer stockMinimo,

        @Size(
                max = 255,
                message =
                        "La observación no puede superar los 255 caracteres"
        )
        String observacion

) {
}