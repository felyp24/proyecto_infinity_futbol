package com.infinityfutbol.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarSaldoCreditoRequest(

        @NotNull(
                message = "El nuevo saldo es obligatorio"
        )
        @Min(
                value = 0,
                message = "El saldo no puede ser negativo"
        )
        @Max(
                value = 9999,
                message = "El saldo no puede superar los 9999 créditos"
        )
        Integer nuevoSaldo,

        @NotBlank(
                message = "Debe indicar el motivo del ajuste"
        )
        @Size(
                max = 100,
                message = "El motivo no puede superar los 100 caracteres"
        )
        String motivo

) {
}