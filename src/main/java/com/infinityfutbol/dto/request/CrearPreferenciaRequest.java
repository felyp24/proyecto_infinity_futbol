package com.infinityfutbol.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearPreferenciaRequest(

        @NotBlank(
                message =
                        "Debe seleccionar un paquete de créditos"
        )
        @Size(
                max = 20,
                message =
                        "El identificador del paquete no es válido"
        )
        String idPaqueteCredito,

        @Size(
                max = 50,
                message =
                        "El código del cupón no puede superar los 50 caracteres"
        )
        String codigoCupon

) {
}