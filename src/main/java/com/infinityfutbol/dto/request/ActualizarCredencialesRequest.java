package com.infinityfutbol.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarCredencialesRequest(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(
                max = 50,
                message = "El nombre de usuario no puede superar los 50 caracteres"
        )
        String nuevoUsername,

        @NotBlank(message = "Debes ingresar tu contraseña actual")
        String passwordActual,

        String nuevaPassword,

        String confirmarPassword

) {
}