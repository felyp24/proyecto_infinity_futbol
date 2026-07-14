package com.infinityfutbol.dto.request;

import com.infinityfutbol.entity.enums.NombreRol;
import jakarta.validation.constraints.NotNull;

public record CambiarRolRequest(

        @NotNull(message = "Debe seleccionar un rol")
        NombreRol rol

) {
}