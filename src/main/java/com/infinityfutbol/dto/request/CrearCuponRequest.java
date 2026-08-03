package com.infinityfutbol.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CrearCuponRequest(

        /*
         * Puede enviarse vacío.
         * En ese caso el backend genera el código.
         */
        @Size(
                max = 50,
                message =
                        "El código no puede superar los 50 caracteres"
        )
        String codigo,

        @NotNull(
                message =
                        "El porcentaje de descuento es obligatorio"
        )
        @DecimalMin(
                value = "1.00",
                message =
                        "El descuento mínimo es 1%"
        )
        @DecimalMax(
                value = "90.00",
                message =
                        "El descuento máximo es 90%"
        )
        @Digits(
                integer = 2,
                fraction = 2,
                message =
                        "El porcentaje no tiene un formato válido"
        )
        BigDecimal porcentajeDescuento,

        @NotNull(
                message =
                        "La fecha de inicio es obligatoria"
        )
        LocalDate fechaInicio,

        @NotNull(
                message =
                        "La fecha de expiración es obligatoria"
        )
        LocalDate fechaExpiracion

) {
}